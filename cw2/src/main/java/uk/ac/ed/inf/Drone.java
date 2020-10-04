package uk.ac.ed.inf;


import uk.ac.ed.inf.backend.SensorService;

import java.io.IOException;
import java.util.List;

public class Drone {

    private RoutePlanner routePlanner;
    private List<Sensor> sensors;
    private DroneLogger logger;

    public Drone(DroneLogger logger, Map map, SensorService sensorService) throws IOException {
        this.logger = logger;
        this.sensors = sensorService.getSensors();
        this.routePlanner = new RoutePlanner(map, (List) this.sensors);
    }
}
