package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.io.IOException;

public class ReadingLogger extends DroneLogger {

    public ReadingLogger(Point initalPos, String loggingPath) throws IOException {
        super(initalPos, loggingPath);
    }

    @Override
    public void log(Point newPos, String read_sensor) {

    }
}
