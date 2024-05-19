package com.example.bts.model;

public class LocationData {
    private double latitude;
    private double longitude;

    public LocationData() {
        // Default constructor required for calls to DataSnapshot.getValue(LocationData.class)
    }

    public LocationData(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
