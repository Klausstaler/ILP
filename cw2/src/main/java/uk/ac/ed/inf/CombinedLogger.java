package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CombinedLogger extends DroneLogger {

    private List<DroneLogger> loggers = new ArrayList<>();

    public CombinedLogger(Point initialPos, String date, Class<?>... loggerClasses) throws
            IllegalAccessException, InvocationTargetException, InstantiationException {

        for (Class<?> loggerClass : loggerClasses) {
            Object object = loggerClass.getConstructors()[0].newInstance(initialPos, date); //
            // assuming each logger class only implements one Constructor
            this.loggers.add((DroneLogger) object);
        }
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
