package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;

public class DashboardController {

    private User loggedInUser;

    // 🔹 RECEIVE LOGGED IN USER
    public void setUser(User user) {
        this.loggedInUser = user;
        System.out.println("Welcome: " + user.getUsername());
    }

    // ================= OPEN CREATE TRIP =================
    public void openCreateTrip() {

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/view/create_trip.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            // 🔹 PASS USER TO NEXT CONTROLLER
            CreateTripController controller = loader.getController();
            controller.setUser(loggedInUser);

            stage.setTitle("Create Trip");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= OPEN VIEW TRIPS =================
    public void openViewTrips() {

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/view/view_trips.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            // 🔹 PASS USER TO VIEW TRIPS CONTROLLER
            ViewTripsController controller = loader.getController();
            controller.setUser(loggedInUser);

            stage.setTitle("View Trips");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}