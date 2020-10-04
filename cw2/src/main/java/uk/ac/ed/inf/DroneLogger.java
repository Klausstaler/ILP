package uk.ac.ed.inf;


import org.locationtech.jts.geom.Point;

import java.io.FileWriter;
import java.io.IOException;

public abstract class DroneLogger {

    protected Point position;
    protected FileWriter file;

    public DroneLogger() {}

    public DroneLogger(Point initialPos, String loggingPath) throws IOException {
        this.position = initialPos;
        this.file = new FileWriter(loggingPath);
    }

    abstract public void log(Point newPos, Sensor read_sensor) throws IOException;

    abstract public void close() throws IOException;
}
