package com.example.bts.model;

public class CarData {
    private String color;
    private String name;
    private String number;

    // Default constructor required for Firebase
    public CarData() {
    }

    public CarData(String color, String name, String number) {
        this.color = color;
        this.name = name;
        this.number = number;
    }

    // Getters and setters for car attributes
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
