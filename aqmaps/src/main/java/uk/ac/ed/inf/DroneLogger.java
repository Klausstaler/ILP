package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Abstract base class for the DroneLoggers.
 */
public abstract class DroneLogger implements Closeable {

    protected Coordinate position; // current position of drone
    protected FileWriter file; // file to write output to

    public DroneLogger(Coordinate initialPos, String loggingPath) throws IOException {
        this.position = initialPos;
        this.file = new FileWriter(loggingPath);
    }

    /**
     * Does the required logging.
     *
     * @param newPos      the new position we need to log
     * @param read_sensor The sensor read during move
     * @throws IOException
     */
    abstract public void log(Coordinate newPos, Sensor read_sensor) throws IOException;
}
