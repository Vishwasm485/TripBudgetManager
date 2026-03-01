package controller;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Trip;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewTripsController {

    @FXML private TableView<Trip> tripTable;
    @FXML private TableColumn<Trip, String> nameCol;
    @FXML private TableColumn<Trip, Integer> daysCol;
    @FXML private TableColumn<Trip, Double> budgetCol;

    private User loggedInUser;

    // 🔹 RECEIVE USER FROM DASHBOARD
    public void setUser(User user) {
        this.loggedInUser = user;
        loadTrips();
    }

    // ================= LOAD TRIPS =================
    private void loadTrips() {

        if (loggedInUser == null) return;

        ObservableList<Trip> list = FXCollections.observableArrayList();

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        daysCol.setCellValueFactory(new PropertyValueFactory<>("days"));
        budgetCol.setCellValueFactory(new PropertyValueFactory<>("budget"));

        String sql = "SELECT * FROM trips WHERE user_id=?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, loggedInUser.getId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
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

        tripTable.setItems(list);
    }
}