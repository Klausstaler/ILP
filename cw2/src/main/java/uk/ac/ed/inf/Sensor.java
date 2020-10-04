package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.Objects;

public class Sensor {

    private String location;
    private double battery;
    private Double reading;

    private Point coordinate;

    public Sensor(String location, double battery) {
        this.location = location;
        this.battery = battery;
    }

    public Sensor(String location, double battery, double reading) {
        this(location, battery);
        this.reading = reading;
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

    public Point getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Point coordinate) {
        this.coordinate = coordinate;
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
                ", coordinates=" + coordinate +
                '}';
    }
}
