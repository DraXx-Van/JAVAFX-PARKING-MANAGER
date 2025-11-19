package com.example.collegeproject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import model.slots;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class slotscardController implements Initializable {

    @FXML
    private Label isAvailable;

    @FXML
    private Label vehicle_number;

    @FXML
    private ImageView isAvailableImg;

    @FXML
    private Label slot_name;

    @FXML
    private Label vehicle_type;

    @FXML
    private Button removebutton;

    @FXML
    private HBox box;

    private slots slots_save;

    private String[] colors = {"e5f1fd","e5def0","d6edd9","f6f0d8"};

    public void setData(slots slots){
        this.slots_save = slots;
        slot_name.setText(slots.getSlot_name());
        vehicle_type.setText(slots.getVehicle_type());
        isAvailable.setText(slots.isAvailable() ? "Available" : "Occupied");
        isAvailable.setStyle(slots.isAvailable() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        box.setStyle("-fx-background-color :" + (colors[(int)(Math.random() *colors.length)]) +";"+
                "-fx-background-radius : 15;" +
                "-fx-effect: dropShadow(three-pass-box,rgba(0,0,0,0.1),10,0,0,10);");

        String imagePath = slots.isAvailable()
                ? "/com/example/collegeproject/images/Empty.png"
                : "/com/example/collegeproject/images/Filled.png";

        InputStream imageStream = getClass().getResourceAsStream(imagePath);

        if (imageStream == null) {
            System.err.println("⚠️ Image not found: " + imagePath);
        } else {
            isAvailableImg.setImage(new Image(imageStream));
        }

        getVehicleNum(slots.getSlot_name());

        if(isAvailable.getText().equals("Occupied")){
            removebutton.setDisable(true);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            removebutton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setContentText("YOU WANT TO DELETE THIS SLOT ?");
                    alert.show();
                    DBUtils.removeSlot(slots_save.getSlot_name());
                    SceneUtils.ChangeScene(event,"manage.fxml");
                }
            });
    }

    public void getVehicleNum(String slotname){
        String vehicle_num = DBUtils.getVehicleNumber(slotname);
        if(vehicle_num.equals("notfound")){
            vehicle_number.setVisible(false);
        }else{
            vehicle_number.setText(vehicle_num);
        }
    }


}
