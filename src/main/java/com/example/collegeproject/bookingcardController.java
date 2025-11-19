package com.example.collegeproject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import model.bookings;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class bookingcardController implements Initializable {

    @FXML
    private HBox box;

    @FXML
    private Label entry_time;

    @FXML
    private Button exitbutton;

    @FXML
    private Label slot_name;

    @FXML
    private Label status;

    @FXML
    private ImageView vehicle_image;

    @FXML
    private Label vehicle_number;

    @FXML
    private Label vehicle_type;

    @FXML
    private Label exittime;

    @FXML
    private Label revenue_text;

    @FXML
    private Label revenue_val;

    private Consumer<bookings> onExitClicked;

    private bookings bookingData;

    private String[] colors = {"e5f1fd","e5def0","d6edd9","f6f0d8"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        exitbutton.setOnAction(this::handleExitClick);
    }

    public void setdata(bookings booking){
        this.bookingData =booking;
        vehicle_number.setText(booking.getVehicleNumber());
        slot_name.setText(booking.getSlotName());
        entry_time.setText(booking.getBookingTime().toString().substring(0,16));
        status.setText(booking.getStatus());
        revenue_val.setText(String.valueOf(Math.floor(booking.getRevenue())));
        if(status.getText().equals("Finished")){
            status.setStyle("-fx-text-fill : Red");
        }

        box.setStyle("-fx-background-color :" + (colors[(int)(Math.random() *colors.length)]) +";"+
                "-fx-background-radius : 15;" +
                "-fx-effect: dropShadow(three-pass-box,rgba(0,0,0,0.1),10,0,0,10);");

        String imagePath;
        if (booking.getVehicleType().equalsIgnoreCase("Four Wheeler")) {
            int imgNum = (int) (Math.random() * 3) + 1; // 1 to 3
            imagePath = "/com/example/collegeproject/images/car/car" + imgNum + ".png";
        } else {
            int imgNum = (int) (Math.random() * 2) + 1; // 1 to 2
            imagePath = "/com/example/collegeproject/images/bike/bike" + imgNum + ".png";
        }
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        if (imageStream == null) {
            System.err.println("⚠️ Image not found: " + imagePath);
        } else {
            vehicle_image.setImage(new Image(imageStream));
        }

        if (booking.getStatus().equalsIgnoreCase("Exited") || booking.getStatus().equalsIgnoreCase("Finished")) {
            exitbutton.setVisible(false);
            exittime.setText(booking.getExitTime().toString().substring(0,16));
            exittime.setVisible(true);
            revenue_text.setVisible(true);
            revenue_val.setVisible(true);

        } else {
            exitbutton.setVisible(true);
            exittime.setVisible(false);
            revenue_text.setVisible(false);
            revenue_val.setVisible(false);
        }

    }

    private void handleExitClick(ActionEvent event) {
        if (onExitClicked != null && bookingData != null) {
            onExitClicked.accept(bookingData);
              //does nothing
//            exitbutton.setVisible(false);
//            revenue_text.setVisible(true);
//            revenue_val.setVisible(true);
        }
    }
    public void setOnExitClicked(Consumer<bookings> handler) {
        this.onExitClicked = handler;
    }
}
