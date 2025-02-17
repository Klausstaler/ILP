package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;

import java.util.Objects;

/**
 * Represents a sensor.
 */
public class Sensor extends Coordinate {

    private String location; // what3words address of the sensor
    private float battery; // battery level
    private Double reading; // sensor reading, null if the battery level is below acceptable
    // threshold and reading is not reliable

    public Sensor(String location, float battery, Double reading, double x, double y) {
        super(x, y);
        this.location = location;
        this.reading = reading;
        this.battery = battery;
    }

    public String getLocation() {
        return location;
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

    @Override
    public String toString() {
        return "Sensor{" +
                "location='" + location + '\'' +
                ", battery=" + battery +
                ", reading=" + reading +
                ", coordinates=" + this.x + " " + this.y +
                '}';
    }
}
