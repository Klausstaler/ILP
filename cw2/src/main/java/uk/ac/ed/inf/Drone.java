package uk.ac.ed.inf;


import org.locationtech.jts.geom.Point;
import uk.ac.ed.inf.backend.SensorService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Drone {

    private Point position;
    private RoutePlanner routePlanner;
    private List<Sensor> sensors;
    private DroneLogger logger;

    public Drone(Point position, DroneLogger logger, Map map, SensorService sensorService) throws IOException {
        this.position = position;
        this.logger = logger;
        this.sensors = sensorService.getSensors();
        List<Point> waypoints = new ArrayList<>();
        waypoints.add(position);
        waypoints.addAll(sensors);
        this.routePlanner = new RoutePlanner(map, waypoints);
    }

    public void navigate() {
    }
}
