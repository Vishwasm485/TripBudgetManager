package controller;

import database.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class CreateTripController {

    @FXML private TextField nameField;
    @FXML private TextField daysField;
    @FXML private TextField budgetField;
    @FXML private Label messageLabel;

    @FXML
    private void handleSave() {

        String sql =
                "INSERT INTO trips(name,days,budget) VALUES(?,?,?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nameField.getText());
            ps.setInt(2, Integer.parseInt(daysField.getText()));
            ps.setDouble(3, Double.parseDouble(budgetField.getText()));

            ps.executeUpdate();

            messageLabel.setText("Trip saved successfully ✔");

            nameField.clear();
            daysField.clear();
            budgetField.clear();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error saving trip");
        }
    }
}