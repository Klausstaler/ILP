package uk.ac.ed.inf;

public class Sensor {

    private String location;
    private double battery;
    private double reading;

    public Sensor(String location, double battery, int reading) {
        this.location = location;
        this.battery = battery;
        this.reading = reading;
    }

    public String getLocation() {
        return location;
    }

    public double getBattery() {
        return battery;
    }

    public double getReading() {
        return reading;
    }
}
