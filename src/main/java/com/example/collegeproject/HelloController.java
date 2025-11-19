package com.example.collegeproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private TextField name;

    @FXML
    private TextField password;

    @FXML
    private TextField username;

    public static String Name;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void Login(ActionEvent event){

        Name = name.getText();
        if(username.getText().equals("admin@mail.com") && password.getText().equals("admin")){
            SceneUtils.ChangeScene(event,"dash.fxml");
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Wrong email or Password");
            alert.show();
        }

    }

}


