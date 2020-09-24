package uk.ac.ed.inf;

import java.util.Objects;

public class Sensor {

    private String location;
    private double battery;
    private Double reading;

    public Sensor(String location, double battery, double reading) {
        this.location = location;
        this.battery = battery;
        this.reading = reading;
    }

    public Sensor(String location, double battery) {
        this.location = location;
        this.battery = battery;
        System.out.println(this.reading);
    }

    public String getLocation() {
        return location;
    }

    public double getBattery() {
        return battery;
    }

    public Double getReading() {
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
