package com.busresevation.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;



/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("mainui.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Bus Reservation System");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}