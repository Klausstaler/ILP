package uk.ac.ed.inf;


import org.locationtech.jts.geom.*;

import java.util.Objects;

public class Sensor extends Point {

    private String location;
    private double battery;
    private Double reading;

    public Sensor(String location, double battery, Double reading, Coordinate coordinate,
                  GeometryFactory factory) {
        super(new GeometryFactory().getCoordinateSequenceFactory().create(new Coordinate[]{coordinate}),
                factory);
        this.location = location;
        this.reading = reading;
        this.battery = battery;
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

    /*
    public void setCoordinate(Point coordinate) {
        this.coordinate = coordinate;
    }
     */
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
                ", coordinates=" + this.getCoordinate() +
                '}';
    }
}
