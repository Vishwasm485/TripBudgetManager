package controller;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Expense;
import model.Trip;

import java.sql.*;
import java.util.*;

public class TripDetailsController {

    @FXML private Label titleLabel;
    @FXML private Label totalLabel;
    @FXML private Label shareLabel;

    @FXML private ComboBox<String> payerBox;
    @FXML private TextField amountField;
    @FXML private TextField descField;

    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense,String> payerCol;
    @FXML private TableColumn<Expense,Double> amountCol;
    @FXML private TableColumn<Expense,String> descCol;

    @FXML private TextArea resultArea;

    private Trip trip;

    // ================= SET TRIP =================
    public void setTrip(Trip trip) {
        this.trip = trip;

        titleLabel.setText("Trip: " + trip.getName());

        payerCol.setCellValueFactory(new PropertyValueFactory<>("payer"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadTravelers();
        loadExpenses();
    }

    // ================= LOAD TRAVELERS =================
    private void loadTravelers() {

        payerBox.getItems().clear();

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps =
                     conn.prepareStatement("SELECT name FROM travelers WHERE trip_id=?")) {

            ps.setInt(1, trip.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                payerBox.getItems().add(rs.getString("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= ADD EXPENSE =================
    @FXML
    private void addExpense() {

        if (payerBox.getValue() == null) {
            showAlert("Please select who paid.");
            return;
        }

        if (amountField.getText().isBlank()) {
            showAlert("Please enter amount.");
            return;
        }

        try {
            double amt = Double.parseDouble(amountField.getText());

            try (Connection conn = DBConnection.connect();
                 PreparedStatement ps =
                         conn.prepareStatement(
                                 "INSERT INTO expenses(trip_id,payer,amount,description) VALUES(?,?,?,?)")) {

                ps.setInt(1, trip.getId());
                ps.setString(2, payerBox.getValue());
                ps.setDouble(3, amt);
                ps.setString(4, descField.getText());

                ps.executeUpdate();
            }

            amountField.clear();
            descField.clear();

            loadExpenses();

        } catch (NumberFormatException e) {
            showAlert("Amount must be numeric.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= LOAD EXPENSES =================
    private void loadExpenses() {

        ObservableList<Expense> list = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps =
                     conn.prepareStatement("SELECT * FROM expenses WHERE trip_id=?")) {

            ps.setInt(1, trip.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Expense(
                        rs.getInt("id"),
                        rs.getString("payer"),
                        rs.getDouble("amount"),
                        rs.getString("description")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        expenseTable.setItems(list);
    }

    // ================= DELETE EXPENSE =================
    @FXML
    private void deleteExpense() {

        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps =
                     conn.prepareStatement("DELETE FROM expenses WHERE id=?")) {

            ps.setInt(1, selected.getId());
            ps.executeUpdate();

            loadExpenses();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= CALCULATE SPLIT =================
    @FXML
    private void calculateSplit() {

        Map<String, Double> paidMap = new HashMap<>();
        Map<String, Double> balance = new HashMap<>();
        Map<String, List<String>> contributions = new HashMap<>();
        List<String> people = new ArrayList<>();

        try (Connection conn = DBConnection.connect()) {

            PreparedStatement ps1 =
                    conn.prepareStatement("SELECT name FROM travelers WHERE trip_id=?");
            ps1.setInt(1, trip.getId());
            ResultSet r1 = ps1.executeQuery();

            while (r1.next()) {
                String name = r1.getString("name");
                people.add(name);
                paidMap.put(name, 0.0);
                balance.put(name, 0.0);
                contributions.put(name, new ArrayList<>());
            }

            PreparedStatement ps2 =
                    conn.prepareStatement("SELECT payer,amount,description FROM expenses WHERE trip_id=?");
            ps2.setInt(1, trip.getId());
            ResultSet r2 = ps2.executeQuery();

            double total = 0;

            while (r2.next()) {
                String payer = r2.getString("payer");
                double amt = r2.getDouble("amount");
                String desc = r2.getString("description");

                total += amt;

                paidMap.put(payer, paidMap.get(payer) + amt);
                contributions.get(payer)
                        .add("• Paid " + String.format("%.2f", amt) + " Rs for " + desc);
            }

            if (people.isEmpty()) {
                resultArea.setText("No travelers found.");
                return;
            }

            double share = total / people.size();

            totalLabel.setText(String.format("%.2f Rs", total));
            shareLabel.setText(String.format("%.2f Rs", share));

            for (String p : people) {
                balance.put(p, paidMap.get(p) - share);
            }

            List<Map.Entry<String, Double>> creditors = new ArrayList<>();
            List<Map.Entry<String, Double>> debtors = new ArrayList<>();

            for (var e : balance.entrySet()) {
                if (e.getValue() > 0) creditors.add(e);
                else if (e.getValue() < 0) debtors.add(e);
            }

            StringBuilder result = new StringBuilder();

            result.append("========= CONTRIBUTIONS =========\n\n");

            for (String p : people) {
                result.append(p).append(":\n");

                if (contributions.get(p).isEmpty()) {
                    result.append("• No expenses paid\n");
                } else {
                    for (String line : contributions.get(p)) {
                        result.append(line).append("\n");
                    }
                }

                result.append("\n");
            }

            result.append("========= FINAL SETTLEMENT =========\n\n");

            int i = 0, j = 0;

            while (i < debtors.size() && j < creditors.size()) {

                var d = debtors.get(i);
                var c = creditors.get(j);

                double debt = -d.getValue();
                double credit = c.getValue();

                double pay = Math.min(debt, credit);

                result.append(d.getKey())
                        .append(" has to pay ")
                        .append(String.format("%.2f", pay))
                        .append(" Rs to ")
                        .append(c.getKey())
                        .append("\n");

                d.setValue(-(debt - pay));
                c.setValue(credit - pay);

                if (Math.abs(d.getValue()) < 0.01) i++;
                if (Math.abs(c.getValue()) < 0.01) j++;
            }

            resultArea.setText(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= ALERT =================
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}