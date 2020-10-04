package uk.ac.ed.inf;

import org.locationtech.jts.geom.Point;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CombinedLogger extends DroneLogger {

    private List<DroneLogger> loggers;

    public CombinedLogger(Point initialPos, String date, DroneLogger... loggerClasses) {

        this.loggers = Arrays.asList(loggerClasses);
        System.out.println("Combined logger initialized...");
    }

    @Override
    public void log(Point newPos, Sensor read_sensor) throws IOException {
        for( DroneLogger logger : this.loggers) {
            logger.log(newPos, read_sensor);
        }
    }

    @Override
    public void close() throws IOException {
        for( DroneLogger logger : this.loggers) {
            logger.close();
        }
    }
}
