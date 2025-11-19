package com.example.collegeproject;

import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(false);
        //scene.getStylesheets().add(getClass().getResource("/com/example/collegeproject/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Parking Manager");
        Image icon = new Image(getClass().getResourceAsStream("logo.png"));
        stage.getIcons().add(icon);
        //stage.setMaximized(true);
        stage.show();
    }
}
