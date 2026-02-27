package controller;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Expense;
import model.Traveler;
import model.Trip;

import java.sql.*;
import java.util.*;

public class TripDetailsController {

    // ===== HEADER =====
    @FXML private Label titleLabel;
    @FXML private Label totalLabel;
    @FXML private Label shareLabel;

    // ===== TRAVELERS =====
    @FXML private TextField nameField;
    @FXML private TableView<Traveler> table;
    @FXML private TableColumn<Traveler,Integer> idCol;
    @FXML private TableColumn<Traveler,String> nameCol;

    // ===== EXPENSE INPUT =====
    @FXML private ComboBox<String> payerBox;
    @FXML private TextField amountField;
    @FXML private TextField descField;

    // ===== EXPENSE TABLE =====
    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense,String> payerCol;
    @FXML private TableColumn<Expense,Double> amountCol;
    @FXML private TableColumn<Expense,String> descCol;

    // ===== RESULT =====
    @FXML private TextArea resultArea;

    private Trip trip;

    // ======================================================
    // SET TRIP
    // ======================================================
    public void setTrip(Trip trip) {
        this.trip = trip;
        titleLabel.setText("Trip: " + trip.getName());

        // Traveler table config
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Expense table config
        payerCol.setCellValueFactory(new PropertyValueFactory<>("payer"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadTravelers();
        loadExpenses();
    }

    // ======================================================
    // TRAVELERS
    // ======================================================
    @FXML
    private void addTraveler() {

        if(nameField.getText().isBlank()) return;

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps =
                     conn.prepareStatement(
                             "INSERT INTO travelers(trip_id,name) VALUES(?,?)")) {

            ps.setInt(1, trip.getId());
            ps.setString(2, nameField.getText().trim());
            ps.executeUpdate();

            nameField.clear();
            loadTravelers();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTravelers() {

        ObservableList<Traveler> list =
                FXCollections.observableArrayList();

        payerBox.getItems().clear();

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps =
                     conn.prepareStatement(
                             "SELECT * FROM travelers WHERE trip_id=?")) {

            ps.setInt(1, trip.getId());
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                String name = rs.getString("name");

                list.add(new Traveler(
                        rs.getInt("id"),
                        name
                ));

                payerBox.getItems().add(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        table.setItems(list);
    }

    // ======================================================
    // EXPENSES
    // ======================================================
    @FXML
    private void addExpense() {

        if(payerBox.getValue() == null ||
                amountField.getText().isBlank()) return;

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps =
                     conn.prepareStatement(
                             "INSERT INTO expenses(trip_id,payer,amount,description) VALUES(?,?,?,?)")) {

            ps.setInt(1, trip.getId());
            ps.setString(2, payerBox.getValue());
            ps.setDouble(3, Double.parseDouble(amountField.getText()));
            ps.setString(4, descField.getText());

            ps.executeUpdate();

            amountField.clear();
            descField.clear();

            loadExpenses();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String msg){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void loadExpenses(){

        ObservableList<Expense> list =
                FXCollections.observableArrayList();

        try(Connection conn = DBConnection.connect();
            PreparedStatement ps =
                    conn.prepareStatement(
                            "SELECT * FROM expenses WHERE trip_id=?")){

            ps.setInt(1, trip.getId());
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                list.add(new Expense(
                        rs.getInt("id"),
                        rs.getString("payer"),
                        rs.getDouble("amount"),
                        rs.getString("description")
                ));
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        expenseTable.setItems(list);
    }

    @FXML
    private void deleteExpense(){

        Expense selected =
                expenseTable.getSelectionModel().getSelectedItem();

        if(selected == null) return;

        try(Connection conn = DBConnection.connect();
            PreparedStatement ps =
                    conn.prepareStatement(
                            "DELETE FROM expenses WHERE id=?")){

            ps.setInt(1, selected.getId());
            ps.executeUpdate();

            loadExpenses();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    // ======================================================
    // SPLIT LOGIC
    // ======================================================
    @FXML
    private void calculateSplit() {

        Map<String, Double> paidMap = new HashMap<>();
        Map<String, Double> balance = new HashMap<>();
        Map<String, List<String>> contributionDetails = new HashMap<>();
        List<String> people = new ArrayList<>();

        try (Connection conn = DBConnection.connect()) {

            // Load travelers
            PreparedStatement ps1 =
                    conn.prepareStatement("SELECT name FROM travelers WHERE trip_id=?");
            ps1.setInt(1, trip.getId());
            ResultSet r1 = ps1.executeQuery();

            while (r1.next()) {
                String name = r1.getString("name");
                people.add(name);
                paidMap.put(name, 0.0);
                balance.put(name, 0.0);
                contributionDetails.put(name, new ArrayList<>());
            }

            // Load expenses
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
                contributionDetails.get(payer)
                        .add("• Paid " + String.format("%.2f", amt) + " Rs for " + desc);
            }

            if (people.isEmpty()) {
                resultArea.setText("No travelers added.");
                return;
            }

            double share = total / people.size();

            totalLabel.setText("Total Trip Cost: " + String.format("%.2f", total) + " Rs");
            shareLabel.setText("Per Person Share: " + String.format("%.2f", share) + " Rs");

            // Calculate net balance
            for (String p : people) {
                double net = paidMap.get(p) - share;
                balance.put(p, net);
            }

            // Find top contributor
            String topPerson = null;
            double maxPaid = 0;
            for (String p : paidMap.keySet()) {
                if (paidMap.get(p) > maxPaid) {
                    maxPaid = paidMap.get(p);
                    topPerson = p;
                }
            }

            // Separate creditors & debtors
            List<Map.Entry<String, Double>> creditors = new ArrayList<>();
            List<Map.Entry<String, Double>> debtors = new ArrayList<>();

            for (var e : balance.entrySet()) {
                if (e.getValue() > 0) creditors.add(e);
                else if (e.getValue() < 0) debtors.add(e);
            }

            StringBuilder result = new StringBuilder();

            result.append("========== TRIP SUMMARY ==========\n\n");
            result.append("Total Trip Cost: ").append(String.format("%.2f", total)).append(" Rs\n");
            result.append("Each Person Should Pay: ").append(String.format("%.2f", share)).append(" Rs\n\n");

            result.append("Top Contributor: ")
                    .append(topPerson)
                    .append(" (")
                    .append(String.format("%.2f", maxPaid))
                    .append(" Rs)\n\n");

            result.append("========== CONTRIBUTIONS ==========\n\n");

            for (String p : people) {
                result.append(p).append(":\n");
                if (contributionDetails.get(p).isEmpty()) {
                    result.append("• No expenses paid\n");
                } else {
                    for (String detail : contributionDetails.get(p)) {
                        result.append(detail).append("\n");
                    }
                }
                result.append("\n");
            }

            result.append("========== FINAL SETTLEMENT ==========\n\n");

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
}