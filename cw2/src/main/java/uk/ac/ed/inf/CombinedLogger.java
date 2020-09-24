package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.io.IOException;

public class CombinedLogger extends DroneLogger {

    private DroneLogger flightPathLogger;
    private DroneLogger readingLogger;

    public CombinedLogger(Point initialPos, String date) throws IOException {
        this.flightPathLogger = new FlightPathLogger(initialPos, date);
        this.readingLogger = new ReadingLogger(initialPos, date);
        System.out.println("Combined logger initialized...");
    }

    @Override
    public void log(Point newPos, Sensor read_sensor) throws IOException {
        this.flightPathLogger.log(newPos, read_sensor);
        this.readingLogger.log(newPos, read_sensor);
    }

    @Override
    public void close() throws IOException {
        this.flightPathLogger.close();
        this.readingLogger.close();
    }
}
