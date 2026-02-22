package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardController {

    public void openCreateTrip() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/view/create_trip.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Create Trip");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void openViewTrips() {

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/view/view_trips.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("View Trips");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}