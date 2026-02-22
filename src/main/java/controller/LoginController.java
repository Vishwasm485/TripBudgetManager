package controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import database.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin() {

        String sql =
                "SELECT * FROM users WHERE email=? AND password=?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emailField.getText());
            ps.setString(2, passwordField.getText());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                FXMLLoader loader =
                        new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));

                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Dashboard");
                stage.show();

// close login window
                ((Stage) emailField.getScene().getWindow()).close();
            } else {
                messageLabel.setText("Invalid login");
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Database error");
        }
    }
}