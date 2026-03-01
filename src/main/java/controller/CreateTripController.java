package controller;

import database.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class CreateTripController {

    @FXML private TextField nameField;
    @FXML private TextField daysField;
    @FXML private TextField budgetField;
    @FXML private Label messageLabel;

    private User loggedInUser;

    // 🔹 Receive logged-in user from Dashboard
    public void setUser(User user) {
        this.loggedInUser = user;
    }

    @FXML
    private void handleSave() {

        if (loggedInUser == null) {
            messageLabel.setText("User not logged in.");
            return;
        }

        if (nameField.getText().isBlank() ||
                daysField.getText().isBlank() ||
                budgetField.getText().isBlank()) {

            messageLabel.setText("All fields required.");
            return;
        }

        String sql =
                "INSERT INTO trips(user_id,name,days,budget) VALUES(?,?,?,?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, loggedInUser.getId());
            ps.setString(2, nameField.getText());
            ps.setInt(3, Integer.parseInt(daysField.getText()));
            ps.setDouble(4, Double.parseDouble(budgetField.getText()));

            ps.executeUpdate();

            messageLabel.setText("Trip saved successfully ✔");

            nameField.clear();
            daysField.clear();
            budgetField.clear();

        } catch (NumberFormatException e) {
            messageLabel.setText("Days and Budget must be numeric.");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error saving trip");
        }
    }
}