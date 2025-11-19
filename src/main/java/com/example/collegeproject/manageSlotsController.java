package com.example.collegeproject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.slots;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class manageSlotsController implements Initializable {

    @FXML
    private Button bt_addslot;

    @FXML
    private RadioButton rb_fourWheeler;

    @FXML
    private RadioButton rb_twoWheeler;

    @FXML
    private TextField tf_slotname;

    @FXML
    private GridPane slotsGrid;

    @FXML
    private Button searchbtn;

    @FXML
    private TextField tf_search;

    @FXML
    private Label Name;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Name.setText(HelloController.Name);
        ToggleGroup toggleGroup = new ToggleGroup();
        rb_fourWheeler.setToggleGroup(toggleGroup);
        rb_twoWheeler.setToggleGroup(toggleGroup);
        rb_twoWheeler.setSelected(true);

         bt_addslot.setOnAction(new EventHandler<ActionEvent>() {
             @Override
             public void handle(ActionEvent event) {
                 String vehiceltype = ((RadioButton) toggleGroup.getSelectedToggle()).getText();
                 if(!tf_slotname.getText().trim().isEmpty()){
                     DBUtils.manage_addslot(event,tf_slotname.getText().trim().toUpperCase(),vehiceltype);
                     loadSlots();
                 }else{
                     System.out.println("Enter something");
                     Alert alert = new Alert(Alert.AlertType.ERROR);
                     alert.setContentText("Please Enter Slot Name");
                     alert.show();
                 }
             }
         });

         searchbtn.setOnAction(new EventHandler<ActionEvent>() {
             @Override
             public void handle(ActionEvent event) {
                 slotsGrid.getChildren().clear();
                 if(tf_search.getText().trim().isEmpty()){
                     Alert alert = new Alert(Alert.AlertType.ERROR);
                     alert.setContentText("Please Enter Slot Name to be searched");
                     alert.show();
                 }else {
                     slots slotdata = DBUtils.searchSlots(event,tf_search.getText().toUpperCase());
                     int column = 0;
                     int row = 1;
                     try{
                         FXMLLoader fxmlLoader = new FXMLLoader();
                         fxmlLoader.setLocation(getClass().getResource("slots.fxml"));
                         HBox slotsbox = fxmlLoader.load();
                         slotscardController slotcard = fxmlLoader.getController();
                         slotcard.setData(slotdata);

                         slotsGrid.add(slotsbox,column++,row);
                         GridPane.setMargin(slotsbox, new Insets(10,20,10,20));
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 }
             }
         });

         loadSlots();
    }

    public void loadSlots(){
        slotsGrid.getChildren().clear();
        List<slots> slotsList = DBUtils.getAllSlots();
        int column = 0;
        int row = 1;
        try {
            for(slots slots : slotsList){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("slots.fxml"));
                //fxmlLoader.setLocation(getClass().getResource("slots.fxml"));
                HBox slotsbox = fxmlLoader.load();
                slotscardController slotcard = fxmlLoader.getController();
                slotcard.setData(slots);

                if(column == 4){
                    column = 0;
                    ++row;
                }

                slotsGrid.add(slotsbox,column++,row);
                GridPane.setMargin(slotsbox, new Insets(10,20,10,20));

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void dash(ActionEvent event){
        SceneUtils.ChangeScene(event,"dash.fxml");
    }
    public void manage(ActionEvent event){
        SceneUtils.ChangeScene(event,"manage.fxml");
    }
    public void bookings(ActionEvent event){
        SceneUtils.ChangeScene(event,"booking.fxml");
    }

}
