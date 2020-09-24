package uk.ac.ed.inf;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sensor sensor = (Sensor) o;
        return getLocation().equals(sensor.getLocation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocation());
    }
}
