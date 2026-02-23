package controller;

import database.DBConnection;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;

import java.sql.*;
import java.util.*;

public class TripDetailsController {

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TableView<Traveler> table;
    @FXML private TableColumn<Traveler,Integer> idCol;
    @FXML private TableColumn<Traveler,String> nameCol;

    @FXML private ComboBox<String> payerBox;
    @FXML private TextField amountField;
    @FXML private TextField descField;
    @FXML private TextArea resultArea;
    @FXML private Label totalLabel;
    @FXML private Label shareLabel;
    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense,String> payerCol;
    @FXML private TableColumn<Expense,Double> amountCol;
    @FXML private TableColumn<Expense,String> descCol;
    private Trip trip;

    public void setTrip(Trip trip) {
        this.trip = trip;
        titleLabel.setText("Trip: " + trip.getName());

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        payerCol.setCellValueFactory(new PropertyValueFactory<>("payer"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadExpenses();
    }

    // ---------- TRAVELERS ----------
    @FXML
    private void addTraveler() {

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps =
                     conn.prepareStatement("INSERT INTO travelers(trip_id,name) VALUES(?,?)")) {

            ps.setInt(1, trip.getId());
            ps.setString(2, nameField.getText());
            ps.executeUpdate();

            nameField.clear();
            loadTravelers();

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadTravelers() {

        ObservableList<Traveler> list = FXCollections.observableArrayList();
        payerBox.getItems().clear();

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps =
                     conn.prepareStatement("SELECT * FROM travelers WHERE trip_id=?")) {

            ps.setInt(1, trip.getId());
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                String name = rs.getString("name");
                list.add(new Traveler(rs.getInt("id"), name));
                payerBox.getItems().add(name);
            }

        } catch (Exception e) { e.printStackTrace(); }

        table.setItems(list);
    }

    // ---------- EXPENSE ----------
    @FXML
    private void addExpense() {

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

        } catch (Exception e) { e.printStackTrace(); }
    }

    // ---------- SPLIT LOGIC ----------
    @FXML
    private void calculateSplit() {

        Map<String, Double> balance = new HashMap<>();
        List<String> people = new ArrayList<>();

        try (Connection conn = DBConnection.connect()) {

            // Load travelers
            PreparedStatement ps1 =
                    conn.prepareStatement("SELECT name FROM travelers WHERE trip_id=?");
            ps1.setInt(1, trip.getId());
            ResultSet r1 = ps1.executeQuery();

            while(r1.next()) {
                String name = r1.getString("name");
                people.add(name);
                balance.put(name,0.0);
            }

            // Load expenses
            PreparedStatement ps2 =
                    conn.prepareStatement("SELECT payer,amount FROM expenses WHERE trip_id=?");
            ps2.setInt(1, trip.getId());
            ResultSet r2 = ps2.executeQuery();

            double total = 0;

            while(r2.next()) {
                String payer = r2.getString("payer");
                double amt = r2.getDouble("amount");

                total += amt;
                balance.put(payer, balance.get(payer)+amt);
            }

            double share = total / people.size();
            String topPerson = null;
            double max = 0;

            for(String p: people){
                double paidAmount = balance.get(p)+share; // original paid
                if(paidAmount > max){
                    max = paidAmount;
                    topPerson = p;
                }
            }


            totalLabel.setText("Total Trip Cost: " + String.format("%.2f", total));
            shareLabel.setText("Per Person Share: " + String.format("%.2f", share));
            // Convert to net balances
            for(String p : people){
                balance.put(p, balance.get(p) - share);
            }

            // Split into creditors and debtors
            List<Map.Entry<String,Double>> creditors = new ArrayList<>();
            List<Map.Entry<String,Double>> debtors = new ArrayList<>();

            for(var e : balance.entrySet()){
                if(e.getValue() > 0) creditors.add(e);
                else if(e.getValue() < 0) debtors.add(e);
            }

            StringBuilder result = new StringBuilder();

            result.append("Top contributor: ")
                    .append(topPerson)
                    .append(" (")
                    .append(String.format("%.2f", maxPaid))
                    .append(")\n\n");
            result.append("Each should pay: ").append(share).append("\n\n");

            int i=0, j=0;

            while(i < debtors.size() && j < creditors.size()){

                var d = debtors.get(i);
                var c = creditors.get(j);

                double debt = -d.getValue();
                double credit = c.getValue();

                double pay = Math.min(debt, credit);

                result.append(d.getKey())
                        .append(" pays ")
                        .append(c.getKey())
                        .append(" ")
                        .append(String.format("%.2f", pay))
                        .append("\n");

                d.setValue(-(debt-pay));
                c.setValue(credit-pay);

                if(Math.abs(d.getValue()) < 0.01) i++;
                if(Math.abs(c.getValue()) < 0.01) j++;
            }

            resultArea.setText(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadExpenses(){

        ObservableList<Expense> list = FXCollections.observableArrayList();

        try(Connection conn = DBConnection.connect();
            PreparedStatement ps =
                    conn.prepareStatement("SELECT * FROM expenses WHERE trip_id=?")){

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

        }catch(Exception e){ e.printStackTrace(); }

        expenseTable.setItems(list);
    }
    @FXML
    private void deleteExpense(){

        Expense e = expenseTable.getSelectionModel().getSelectedItem();
        if(e==null) return;

        try(Connection conn = DBConnection.connect();
            PreparedStatement ps =
                    conn.prepareStatement("DELETE FROM expenses WHERE id=?")){

            ps.setInt(1, e.getId());
            ps.executeUpdate();

            loadExpenses();

        }catch(Exception ex){ ex.printStackTrace(); }
    }
}