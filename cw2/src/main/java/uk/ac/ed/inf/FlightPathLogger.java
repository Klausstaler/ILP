package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.io.IOException;

public class FlightPathLogger extends DroneLogger {

    private int lineNbr = 1;

    public FlightPathLogger(Point initalPos, String loggingPath) throws IOException {
        super(initalPos, loggingPath);
    }

    @Override
    public void log(Point newPos, String read_sensor) {

    }
}
