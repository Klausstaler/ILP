package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.io.IOException;

public class FlightPathLogger extends DroneLogger {

    private int lineNbr = 1;

    public FlightPathLogger(Point initialPos, String loggingPath) throws IOException {
        super(initialPos, loggingPath);
    }

    @Override
    public void log(Point newPos, String read_sensor) {

    }
}
