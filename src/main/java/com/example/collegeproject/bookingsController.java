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
import javafx.scene.text.Font;
import javafx.stage.Popup;
import model.bookings;
import model.slots;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class bookingsController implements Initializable {

    @FXML
    private Button button_book;

    @FXML
    private Button button_search;

    @FXML
    private Button reportbtn;

    @FXML
    private RadioButton rb_fourWheeler;

    @FXML
    private ComboBox<String> bookings_type;

    @FXML
    private RadioButton rb_twoWheeler;

    @FXML
    private GridPane bookingGrid;

    @FXML
    private TextField tf_searchvehicle;

    @FXML
    private TextField tf_slotname;

    @FXML
    private TextField tf_vehiclenum;

    @FXML
    private ComboBox<String> dateFilter;

    @FXML
    private GridPane twoWheelSlots;

    @FXML
    private GridPane fourWheelSlots;

    @FXML
    private Label Name;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Name.setText(HelloController.Name);

        bookings_type.getItems().addAll("All Bookings","Current Bookings","Finished Bookings");
        bookings_type.getSelectionModel().selectFirst(); // Default
        bookings_type.setOnAction(event -> loadBookings());

        dateFilter.getItems().addAll("Today", "Yesterday", "Last 7 Days", "All Time");
        dateFilter.getSelectionModel().selectFirst(); // Default = Today
        dateFilter.setOnAction(event -> loadBookings());

        ToggleGroup toggleGroup = new ToggleGroup();
        rb_fourWheeler.setToggleGroup(toggleGroup);
        rb_twoWheeler.setToggleGroup(toggleGroup);
        rb_twoWheeler.setSelected(true);

        button_book.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String vehiceltype = ((RadioButton) toggleGroup.getSelectedToggle()).getText();
                if(!tf_slotname.getText().trim().isEmpty() && !tf_vehiclenum.getText().trim().isEmpty()){
                    DBUtils.add_Booking(event,tf_vehiclenum.getText().trim().toUpperCase(),tf_slotname.getText().trim().toUpperCase(),vehiceltype);
                    loadBookings();
                    loadAvailableSlots();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("BOOKING DONE SUCCESSFULLY");
                    alert.show();
                    tf_vehiclenum.clear();
                    tf_slotname.clear();
                }else{
                    System.out.println("Enter something");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please Enter Slot Name");
                    alert.show();
                }
            }
        });

        button_search.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bookingGrid.getChildren().clear();
                if(tf_searchvehicle.getText().trim().isEmpty()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please Enter Vehicle NUmber to be searched");
                    alert.show();
                }else {
                    bookings bookingsdata = DBUtils.searchBookings(event,tf_searchvehicle.getText().toUpperCase());
                    int column = 0;
                    int row = 1;
                    try{
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("bookingsCard.fxml"));
                        HBox bookingsblock = fxmlLoader.load();
                        bookingcardController bookingcard = fxmlLoader.getController();
                        bookingcard.setdata(bookingsdata);

                        bookingGrid.add(bookingsblock,column++,row);
                        GridPane.setMargin(bookingsblock, new Insets(10,20,10,20));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        reportbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String selectedDateRange = dateFilter.getValue();
                try {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setContentText("DO YOU WANT TO GENERATE REPORT ?");
                    alert.show();
                    report.generateReport(selectedDateRange);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        loadAvailableSlots();

        loadBookings();
    }

    public void loadBookings(){
        bookingGrid.getChildren().clear();
        String selectedType = bookings_type.getValue();
        String selectedDateRange = dateFilter.getValue();

        List<bookings> bookingsList;

        if (selectedType.equals("Current Bookings")) {
            bookingsList = DBUtils.getCurrentBookings(selectedDateRange);
        } else if (selectedType.equals("Finished Bookings")) {
            bookingsList = DBUtils.getFinishedBookings(selectedDateRange);
        } else {
            bookingsList = DBUtils.getAllBookings(selectedDateRange);
        }

        int column = 0;
        int row = 1;
        try {
            for(bookings book : bookingsList){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("bookingsCard.fxml"));
                HBox bookingsblock = fxmlLoader.load();
                bookingcardController bookingcard = fxmlLoader.getController();
                bookingcard.setdata(book);

                bookingcard.setOnExitClicked(b -> {
                    DBUtils.markBookingAsFinished(b);
                    loadBookings();
                    // Refresh grid
                });

                if(column == 3){
                    column = 0;
                    ++row;
                }

                bookingGrid.add(bookingsblock,column++,row);
                GridPane.setMargin(bookingsblock, new Insets(10,20,10,20));

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void loadAvailableSlots(){
        List<String> TwoWheelSlots = DBUtils.get2WheelSlots();
        List<String> FourWheelSlots = DBUtils.get4WheelSlots();
        fourWheelSlots.getChildren().clear();
        twoWheelSlots.getChildren().clear();
        int row = 0;
        int row2 = 0;
        for(String slots :TwoWheelSlots){
            Label slot_name = new Label();
            slot_name.setFont(Font.font("Ink Free",23));
            //slot_name.setStyle("-fx-background-color : #8c8676;");
            slot_name.setText(slots);
            twoWheelSlots.add(slot_name,0,row++);
        }
        for(String slots : FourWheelSlots){
            Label slot_name = new Label();
            slot_name.setFont(Font.font("Ink Free",23));
            slot_name.setText(slots);
            fourWheelSlots.add(slot_name,0,row2++);
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
