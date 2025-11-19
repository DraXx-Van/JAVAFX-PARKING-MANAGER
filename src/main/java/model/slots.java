package model;

public class slots {

    private String slot_name;
    private String vehicle_type;
    private boolean isAvailable;

    public slots(String slot_name, String vehicle_type, boolean isAvailable) {
        this.slot_name = slot_name;
        this.vehicle_type = vehicle_type;
        this.isAvailable = isAvailable;
    }

    public String getSlot_name() {
        return slot_name;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
