package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.io.FileWriter;
import java.io.IOException;

public abstract class DroneLogger {

    private Point position;
    private FileWriter file;

    public DroneLogger(Point initialPos, String loggingPath) throws IOException {
        this.position = initialPos;
        this.file = new FileWriter(loggingPath);
    }

    abstract public void log(Point newPos, String read_sensor);
}
