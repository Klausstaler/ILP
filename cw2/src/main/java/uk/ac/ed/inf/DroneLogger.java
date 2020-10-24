package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

import java.io.FileWriter;
import java.io.IOException;

public abstract class DroneLogger {

    protected Coordinate position;
    protected FileWriter file;

    public DroneLogger(Coordinate initialPos, String loggingPath) throws IOException {
        this.position = initialPos;
        this.file = new FileWriter(loggingPath);
    }

    abstract public void log(Coordinate newPos, Sensor read_sensor);

    abstract public void close();
}
