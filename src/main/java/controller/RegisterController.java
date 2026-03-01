package controller;

import database.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    // ================= REGISTER =================
    @FXML
    private void register() throws Exception {

        String email = usernameField.getText();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            showAlert("All fields required.");
            return;
        }

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps =
                     conn.prepareStatement(
                             "INSERT INTO users(email,password) VALUES(?,?)")) {

            ps.setString(1, email);
            ps.setString(2, password);

            ps.executeUpdate();

            showAlert("Registration successful!");

        } catch (SQLException e) {
            showAlert("User already exists.");
        }
    }

    // ================= BACK TO LOGIN =================
    @FXML
    private void backToLogin() {

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/view/login.fxml"));

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}