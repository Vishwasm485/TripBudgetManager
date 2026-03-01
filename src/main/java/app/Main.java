package app;

import database.DBSetup;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        try {

            // Initialize database
            DBSetup.init();

            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/view/login.fxml"));

            Scene scene = new Scene(loader.load(), 400, 250);

            scene.getStylesheets().add(
                    getClass().getResource("/style.css").toExternalForm()
            );

            stage.setTitle("Trip Budget Manager");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            System.out.println("Application failed to start.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}