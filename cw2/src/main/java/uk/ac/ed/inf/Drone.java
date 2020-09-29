package uk.ac.ed.inf;

import com.mapbox.geojson.Point;
import uk.ac.ed.inf.backend.SensorService;

import java.io.IOException;
import java.util.Collection;

public class Drone {

    private RoutePlanner routePlanner;
    private Collection<Sensor> sensors;
    private DroneLogger logger;

    public Drone(DroneLogger logger, Map map, SensorService sensorService) throws IOException {
        this.logger = logger;
        this.sensors = sensorService.getSensors();
        this.routePlanner = new RoutePlanner(map, this.sensorsToPoints());
    }

    private Point[] sensorsToPoints() {
        Point[] points = new Point[this.sensors.size()];
        int idx = 0;
        for (Sensor sensor : this.sensors) {
            points[idx] = sensor.getCoordinates();
            idx++;
        }
        return points;
    }
}
