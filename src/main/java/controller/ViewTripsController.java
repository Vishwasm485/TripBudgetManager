package controller;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Trip;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ViewTripsController {

    @FXML private TableView<Trip> table;
    @FXML private TableColumn<Trip,Integer> idCol;
    @FXML private TableColumn<Trip,String> nameCol;
    @FXML private TableColumn<Trip,Integer> daysCol;
    @FXML private TableColumn<Trip,Double> budgetCol;

    @FXML
    public void initialize() {

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        daysCol.setCellValueFactory(new PropertyValueFactory<>("days"));
        budgetCol.setCellValueFactory(new PropertyValueFactory<>("budget"));

        loadTrips();
    }

    private void loadTrips() {

        ObservableList<Trip> list = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM trips")) {

            while(rs.next()) {

                list.add(new Trip(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("days"),
                        rs.getDouble("budget")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        table.setItems(list);
    }
}