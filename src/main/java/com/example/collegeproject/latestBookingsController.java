package com.example.collegeproject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.latestBookings;

public class latestBookingsController {
    @FXML
    private Label slot_name;

    @FXML
    private Label vehicle_num;

    public void setData(latestBookings bookings){
        vehicle_num.setText(bookings.getVehicle_number());
        slot_name.setText(bookings.getSlot_name());
    }

}
