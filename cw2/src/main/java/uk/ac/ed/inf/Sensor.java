package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.util.Objects;

public class Sensor {

    private String location;
    private double battery;
    private Double reading;

    private Point coordinates;

    public Sensor(String location, double battery, double longitude, double latitude) {
        this.location = location;
        this.battery = battery;
        this.coordinates = Point.fromLngLat(longitude, latitude);
    }

    public Sensor(String location, double battery, double reading, double longitude,
                  double latitude) {
        this(location, battery, longitude, latitude);
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

    public Point getCoordinates() {
        return coordinates;
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
                ", coordinates=" + coordinates +
                '}';
    }
}
