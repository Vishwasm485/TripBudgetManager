package controller;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
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

        table.setRowFactory(tv -> {
            TableRow<Trip> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Trip trip = row.getItem();
                    openTripDetails(trip);
                }
            });

            return row;
        });
    }

    private void openTripDetails(Trip trip) {

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/view/trip_details.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Trip Details");

            TripDetailsController controller = loader.getController();
            controller.setTrip(trip);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
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