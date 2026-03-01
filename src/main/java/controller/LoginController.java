package controller;

import database.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField usernameField;   // this will hold email
    @FXML private PasswordField passwordField;

    // ================= LOGIN =================
    @FXML
    private void login() {

        String email = usernameField.getText();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            showAlert("Please fill all fields.");
            return;
        }

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps =
                     conn.prepareStatement(
                             "SELECT * FROM users WHERE email=? AND password=?")) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                User user = new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password")
                );

                openDashboard(user);

            } else {
                showAlert("Invalid credentials.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database error.");
        }
    }

    // ================= OPEN REGISTER =================
    @FXML
    private void openRegister() {

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/view/register.fxml"));

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= OPEN DASHBOARD =================
    private void openDashboard(User user) {

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            DashboardController controller = loader.getController();
            controller.setUser(user);

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