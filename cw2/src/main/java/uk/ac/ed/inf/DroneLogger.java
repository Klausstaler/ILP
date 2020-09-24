package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.io.FileWriter;
import java.io.IOException;

public abstract class DroneLogger {

    private Point position;
    private FileWriter file;

    public DroneLogger(Point initalPos, String loggingPath) throws IOException {
        this.position = initalPos;
        this.file = new FileWriter(loggingPath);
    }

    abstract public void log(Point newPos, String read_sensor);
}
