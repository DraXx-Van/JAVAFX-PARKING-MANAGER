package model;

public class latestBookings {
    private String vehicle_number;
    private String slot_name;

    public latestBookings(String vehicle_number, String slot_name) {
        this.vehicle_number = vehicle_number;
        this.slot_name = slot_name;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public String getSlot_name() {
        return slot_name;
    }
}
