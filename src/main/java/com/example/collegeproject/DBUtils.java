package com.example.collegeproject;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.bookings;
import model.latestBookings;
import model.slots;

import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

class SceneUtils {
    public static void ChangeScene(ActionEvent event, String FXMLfile) {
        try {
            // Get current scene and stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();

            // Load new FXML root
            Parent newRoot = FXMLLoader.load(SceneUtils.class.getResource(FXMLfile));
            stage.setResizable(true);
            stage.setFullScreen(true);

            // Replace current root with new one
            scene.setRoot(newRoot);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}


public class DBUtils {

    public static final String URL = "jdbc:mysql://localhost:3306/parking_manager";
    private static final String USER = "root";
    private static final String PASSWORD = "0011";

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }


    public static  void manage_addslot(ActionEvent event, String slot_name, String vehicle_type){
        Connection connection = null;
        PreparedStatement checkslotsexist = null;
        PreparedStatement insertslot = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            checkslotsexist = connection.prepareStatement("SELECT * FROM SLOTS WHERE SLOT_NAME = ?");
            checkslotsexist.setString(1,slot_name);
            resultSet = checkslotsexist.executeQuery();

            if(resultSet.next()){
                System.out.println("Slot Exist");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Slot already Exits");
                alert.show();
            }else{
                insertslot = connection.prepareStatement("INSERT INTO SLOTS (SLOT_NAME, VEHICLE_TYPE, IS_AVAILABLE) VALUES (?, ?, TRUE)");
                insertslot.setString(1,slot_name);
                insertslot.setString(2,vehicle_type);
                insertslot.executeUpdate();

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (resultSet != null) resultSet.close();
                if (insertslot != null) insertslot.close();
                if (checkslotsexist != null) checkslotsexist.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public static List<slots> getAllSlots() {
        List<slots> slotList = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT SLOT_NAME, VEHICLE_TYPE, IS_AVAILABLE FROM SLOTS");

            while (resultSet.next()) {
                String slotName = resultSet.getString("SLOT_NAME");
                String vehicleType = resultSet.getString("VEHICLE_TYPE");
                boolean isAvailable = resultSet.getBoolean("IS_AVAILABLE");
                slotList.add(new slots(slotName, vehicleType, isAvailable));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return slotList;
    }

    public static slots searchSlots(ActionEvent event, String slot_name){

        Connection connection = null;
        PreparedStatement search_statement = null;
        ResultSet resultSet = null;
        slots slotcard = null;

        try {
            connection = getConnection();
            search_statement = connection.prepareStatement("SELECT * FROM SLOTS WHERE SLOT_NAME = ?");
            search_statement.setString(1,slot_name);
            resultSet = search_statement.executeQuery();

            if (resultSet.next()) {
                slotcard = new slots(
                        resultSet.getString("SLOT_NAME"),
                        resultSet.getString("VEHICLE_TYPE"),
                        resultSet.getBoolean("IS_AVAILABLE")
                );
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("No slot found with name: " + slot_name);
                alert.show();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (resultSet != null) resultSet.close();
                if (search_statement != null) search_statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return slotcard;
    }

    public static void add_Booking(ActionEvent event,String vehicle_number,String slot_name,String vehicle_type){
        String checkSlot = "SELECT IS_AVAILABLE FROM SLOTS WHERE SLOT_NAME = ? AND VEHICLE_TYPE = ?";
        String insertBooking = "INSERT INTO BOOKINGS (SLOT_NAME, VEHICLE_NUMBER, VEHICLE_TYPE, BOOKING_TIME) VALUES (?, ?, ?, ?)";
        String updateSlot = "UPDATE SLOTS SET IS_AVAILABLE = FALSE WHERE SLOT_NAME = ?";

        Connection connection = null;
        PreparedStatement checkslotsexist = null;
        PreparedStatement addbooking = null;
        PreparedStatement updateStmt = null;
        ResultSet resultSet = null;

        try{
            connection = getConnection();
            checkslotsexist = connection.prepareStatement(checkSlot);
            checkslotsexist.setString(1,slot_name);
            checkslotsexist.setString(2,vehicle_type);
            resultSet = checkslotsexist.executeQuery();

            if(resultSet.next() && resultSet.getBoolean("IS_AVAILABLE")){
                LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                addbooking = connection.prepareStatement(insertBooking);
                addbooking.setString(1,slot_name);
                addbooking.setString(2,vehicle_number);
                addbooking.setString(3,vehicle_type);
                addbooking.setTimestamp(4,Timestamp.valueOf(now));
                addbooking.executeUpdate();

                updateStmt = connection.prepareStatement(updateSlot);
                updateStmt.setString(1, slot_name);
                updateStmt.executeUpdate();

            }else {
                //System.out.println("Slot Exist");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Slot is not Available");
                alert.show();
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (resultSet != null) resultSet.close();
                if (updateStmt != null ) updateStmt.close();
                if (addbooking != null) addbooking.close();
                if (checkslotsexist != null) checkslotsexist.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static List<bookings> getAllBookings(String daterange) {
        List<bookings> bookingsList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM BOOKINGS";

        if ("Today".equalsIgnoreCase(daterange)) {
            query += " WHERE DATE(BOOKING_TIME) = CURDATE()";
        } else if ("Yesterday".equalsIgnoreCase(daterange)) {
            query += " WHERE DATE(BOOKING_TIME) = CURDATE() - INTERVAL 1 DAY";
        } else if ("Last 7 Days".equalsIgnoreCase(daterange)) {
            query += " WHERE BOOKING_TIME >= CURDATE() - INTERVAL 7 DAY";
        }

        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            //"SELECT SLOT_NAME,VEHICLE_NUMBER,VEHICLE_TYPE,BOOKING_TIME,STATUS FROM BOOKINGS"

            while (resultSet.next()) {
                String slotName = resultSet.getString("SLOT_NAME");
                String vehiclename = resultSet.getString("VEHICLE_NUMBER");
                String vehicleType = resultSet.getString("VEHICLE_TYPE");
                Timestamp bookingtime = resultSet.getTimestamp("BOOKING_TIME");
                String status = resultSet.getString("STATUS");
                Timestamp exitTime = resultSet.getTimestamp("EXIT_TIME");
                double revenue = resultSet.getDouble("REVENUE");
                bookingsList.add(new bookings(slotName, vehiclename, vehicleType,bookingtime,status,exitTime,revenue));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bookingsList;
    }

    public static bookings searchBookings(ActionEvent event, String vehicle_number){

        Connection connection = null;
        PreparedStatement search_statement = null;
        ResultSet resultSet = null;
        bookings bookings = null;

        try {
            connection = getConnection();
            search_statement = connection.prepareStatement("SELECT * FROM BOOKINGS WHERE VEHICLE_NUMBER = ?");
            search_statement.setString(1,vehicle_number);
            resultSet = search_statement.executeQuery();

            if (resultSet.next()) {
                bookings = new bookings(
                        resultSet.getString("SLOT_NAME"),
                        resultSet.getString("VEHICLE_NUMBER"),
                        resultSet.getString("VEHICLE_TYPE"),
                        resultSet.getTimestamp("BOOKING_TIME"),
                        resultSet.getString("STATUS"),
                        null,0
                );
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("No Booking found with Vehicle number: " + vehicle_number);
                alert.show();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (resultSet != null) resultSet.close();
                if (search_statement != null) search_statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bookings;
    }

    public static List<bookings> getCurrentBookings(String daterange){
        List<bookings> bookingsList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM BOOKINGS WHERE STATUS = 'Active'";

        if ("Today".equalsIgnoreCase(daterange)) {
            query += "AND DATE(BOOKING_TIME) = CURDATE()";
        } else if ("Yesterday".equalsIgnoreCase(daterange)) {
            query += "AND DATE(BOOKING_TIME) = CURDATE() - INTERVAL 1 DAY";
        } else if ("Last 7 Days".equalsIgnoreCase(daterange)) {
            query += "AND BOOKING_TIME >= CURDATE() - INTERVAL 7 DAY";
        }

        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String slotName = resultSet.getString("SLOT_NAME");
                String vehiclename = resultSet.getString("VEHICLE_NUMBER");
                String vehicleType = resultSet.getString("VEHICLE_TYPE");
                Timestamp bookingtime = resultSet.getTimestamp("BOOKING_TIME");
                String status = resultSet.getString("STATUS");
                bookingsList.add(new bookings(slotName, vehiclename, vehicleType,bookingtime,status,null,0));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bookingsList;

    }

    public static List<bookings> getFinishedBookings(String daterange){
        List<bookings> bookingsList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM BOOKINGS WHERE STATUS = 'Finished'";

        if ("Today".equalsIgnoreCase(daterange)) {
            query += " AND DATE(BOOKING_TIME) = CURDATE()";
        } else if ("Yesterday".equalsIgnoreCase(daterange)) {
            query += " AND DATE(BOOKING_TIME) = CURDATE() - INTERVAL 1 DAY";
        } else if ("Last 7 Days".equalsIgnoreCase(daterange)) {
            query += " AND BOOKING_TIME >= CURDATE() - INTERVAL 7 DAY";
        }

//        if ("Today".equalsIgnoreCase(daterange)) {
//            query += " AND CAST(BOOKING_TIME AS DATE) = CAST(CURRENT_DATE() AS DATE)";
//        } else if ("Yesterday".equalsIgnoreCase(daterange)) {
//            query += " AND CAST(BOOKING_TIME AS DATE) = CAST(DATEADD('DAY', -1, CURRENT_DATE()) AS DATE)";
//        } else if ("Last 7 Days".equalsIgnoreCase(daterange)) {
//            query += " AND BOOKING_TIME >= DATEADD('DAY', -7, CURRENT_DATE())";
//        }
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String slotName = resultSet.getString("SLOT_NAME");
                String vehiclename = resultSet.getString("VEHICLE_NUMBER");
                String vehicleType = resultSet.getString("VEHICLE_TYPE");
                Timestamp bookingtime = resultSet.getTimestamp("BOOKING_TIME");
                String status = resultSet.getString("STATUS");
                Timestamp exitTime = resultSet.getTimestamp("EXIT_TIME");
                double revenue = resultSet.getDouble("REVENUE");
                bookingsList.add(new bookings(slotName, vehiclename, vehicleType,bookingtime,status,exitTime,revenue));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bookingsList;

    }

    public static void markBookingAsFinished(bookings bookings){
        //LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        String updateBookingsSql = "UPDATE bookings SET status='Finished', exit_time=?, revenue=? WHERE vehicle_number=?";
        String updateSlotsSql = "UPDATE SLOTS SET IS_AVAILABLE = ? WHERE SLOT_NAME = ?";
        Connection connection = null;
        PreparedStatement updatebookings = null;
        PreparedStatement updateSlot = null;

        try{
            LocalDateTime exitTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            Duration duration = Duration.between(bookings.getBookingTime().toLocalDateTime(), exitTime);
            double hours = Math.ceil(duration.toMinutes() / 60.0);
            double rate = bookings.getVehicleType().equalsIgnoreCase("Four Wheeler") ? 50 : 20;
            double revenue = hours * rate;

            connection = getConnection();
            updatebookings = connection.prepareStatement(updateBookingsSql);
            updatebookings.setTimestamp(1,Timestamp.valueOf(exitTime));
            updatebookings.setDouble(2,revenue);
            updatebookings.setString(3,bookings.getVehicleNumber());
            updatebookings.executeUpdate();

            updateSlot = connection.prepareStatement(updateSlotsSql);
            updateSlot.setBoolean(1, true);
            updateSlot.setString(2, bookings.getSlotName());
            updateSlot.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (updatebookings != null) updatebookings.close();
                if (updateSlot != null) updateSlot.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static ArrayList<Double> getCount(){
        String sqlquery = "SELECT VEHICLE_TYPE, SUM(REVENUE) AS TotalRevenue FROM BOOKINGS WHERE STATUS = 'Finished' GROUP BY VEHICLE_TYPE";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<Double> revenue = new ArrayList<Double>();
        try {
            connection = getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlquery);
            double temp = 0.0;

            while (resultSet.next()){
                if(resultSet.getString("VEHICLE_TYPE").equals("Four Wheeler")){
                    revenue.add(resultSet.getDouble("TotalRevenue"));
                }else{
                    temp = resultSet.getDouble("TotalRevenue");
                }
            }
            revenue.add(temp);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        //System.out.println(revenue);
        return revenue;

    }

    public static ArrayList<latestBookings> getlatestBookings(){
        ArrayList<latestBookings> latestBookings = new ArrayList<>();
        try(Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT VEHICLE_NUMBER, SLOT_NAME FROM BOOKINGS ORDER BY BOOKING_TIME DESC LIMIT 3");
        ){
            while (resultSet.next()){
                latestBookings.add(new latestBookings(
                        resultSet.getString("VEHICLE_NUMBER"),
                        resultSet.getString("SLOT_NAME")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return latestBookings;
    }

    public static String getVehicleNumber(String slotName){
        String vehiclenum = "";
        Connection connection = null;
        PreparedStatement search_statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            search_statement = connection.prepareStatement("SELECT VEHICLE_NUMBER FROM BOOKINGS WHERE SLOT_NAME = ? AND STATUS = ?");
            search_statement.setString(1,slotName);
            search_statement.setString(2,"Active");
            resultSet = search_statement.executeQuery();
            if(resultSet.next()){
                vehiclenum = resultSet.getString("VEHICLE_NUMBER");
            }else{
                vehiclenum = "notfound";
            }
            return vehiclenum;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (resultSet != null) resultSet.close();
                if (search_statement != null) search_statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //return vehiclenum;
    }

    public static void removeSlot(String slotname){
        Connection connection = null;
        PreparedStatement bookremove = null;
        PreparedStatement slotremove =  null;
        //ResultSet resultSet = null;
        try{
            connection = getConnection();
            bookremove = connection.prepareStatement("DELETE FROM BOOKINGS WHERE SLOT_NAME = ?");
            slotremove = connection.prepareStatement("DELETE FROM SLOTS WHERE SLOT_NAME = ?");
            bookremove.setString(1,slotname);
            slotremove.setString(1,slotname);
            bookremove.executeUpdate();
            slotremove.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if(slotremove != null) slotremove.close();
                if(bookremove != null) bookremove.close();
                if(connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static List<String> get2WheelSlots(){
        List<String>Slots = new ArrayList<>();
        try(Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT SLOT_NAME FROM SLOTS WHERE IS_AVAILABLE = 1 AND VEHICLE_TYPE = 'Two Wheeler'LIMIT 4");
            ) {
            while (resultSet.next()){
                Slots.add(resultSet.getString("SLOT_NAME"));
            }
            return Slots;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> get4WheelSlots(){
        List<String>Slots = new ArrayList<>();
        try(Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT SLOT_NAME FROM SLOTS WHERE IS_AVAILABLE = 1 AND VEHICLE_TYPE = 'Four Wheeler' LIMIT 4");
        ) {
            while (resultSet.next()){
                Slots.add(resultSet.getString("SLOT_NAME"));
            }
            return Slots;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static int Dashboard_Retriever_Slots_Available() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int count = 0;
        try {
            connection = getConnection();
            statement = connection.prepareStatement("SELECT * FROM slots WHERE IS_AVAILABLE = TRUE");
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                count = count + 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
        return count;
    }

    public static int Dashboard_Retriever_Slots_Filled() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int count = 0;
        try {
            connection = getConnection();
            statement = connection.prepareStatement("SELECT * FROM slots WHERE IS_AVAILABLE = FALSE");
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                count = count + 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
        return count;
    }

}

