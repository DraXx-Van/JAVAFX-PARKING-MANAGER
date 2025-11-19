package model;

import java.sql.Time;
import java.sql.Timestamp;

public class bookings {
    private String slotName;
    private String vehicleNumber;
    private String vehicleType;
    private Timestamp bookingTime;
    private String status;
    private Timestamp exittime;
    private Double revenue;


    public bookings(String slotName, String vehicleNumber, String vehicleType, Timestamp bookingTime, String status,Timestamp exittime,double revenue) {
        this.slotName = slotName;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.bookingTime = bookingTime;
        this.status = status;
        this.exittime = exittime;
        this.revenue = revenue;
    }

    public String getSlotName() {
        return slotName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public Timestamp getBookingTime() {
        return bookingTime;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getExitTime() {
        return exittime;
    }

    public Double getRevenue() {
        return revenue;
    }

}
