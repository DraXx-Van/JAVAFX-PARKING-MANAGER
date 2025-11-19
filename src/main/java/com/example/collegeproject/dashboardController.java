package com.example.collegeproject;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.latestBookings;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class dashboardController implements Initializable {

    @FXML
    private Label userName;

    @FXML
    private PieChart piyeChart;

    @FXML
    private GridPane grid;

    @FXML
    private Label slotfilled;

    @FXML
    private Label slotsAvailable;

    @FXML
    private Pane Card;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userName.setText(HelloController.Name);
        ArrayList<Double> revenue = DBUtils.getCount();
        //System.out.println("YOO" +revenue);

        if(revenue.size() == 2){
            Double twoWheeler = revenue.get(1);
            Double fourWheeler = revenue.get(0);
            ObservableList<PieChart.Data> pieChart_revenue=
                    FXCollections.observableArrayList(
                            new PieChart.Data("Four Wheeler", fourWheeler),
                            new PieChart.Data(("Two Wheeler"), twoWheeler));
            pieChart_revenue.forEach(data ->
                    data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(),"Revenue : ",data.pieValueProperty()
                            )
                    ));
            piyeChart.getData().addAll(pieChart_revenue);
        }
        loadlatestBookings();

        Card.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    // Get current scene and stage
                    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                    Scene scene = stage.getScene();

                    // Load new FXML root
                    Parent newRoot = FXMLLoader.load(SceneUtils.class.getResource("booking.fxml"));
                    stage.setResizable(true);
                    stage.setFullScreen(true);

                    // Replace current root with new one
                    scene.setRoot(newRoot);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        try {
            int slotsFill = DBUtils.Dashboard_Retriever_Slots_Filled();
            int slotsAvail = DBUtils.Dashboard_Retriever_Slots_Available();
            slotsAvailable.setText("" + slotsAvail);
            slotfilled.setText("" + slotsFill);
        } catch (SQLException e) {
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

    public void loadlatestBookings(){
        ArrayList<latestBookings> dashbookings= DBUtils.getlatestBookings();
        grid.getChildren().clear();
        int row =0;
        try {
            for(latestBookings bookings : dashbookings){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("dashlatestBookings.fxml"));
                HBox booking = fxmlLoader.load();
                latestBookingsController dashbookingsController = fxmlLoader.getController();
                dashbookingsController.setData(bookings);

                grid.add(booking,0,row++);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

