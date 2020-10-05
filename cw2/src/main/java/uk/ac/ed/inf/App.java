package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;
import uk.ac.ed.inf.backend.ObstacleService;
import uk.ac.ed.inf.backend.SensorService;

public class App 
{
    static {
        System.loadLibrary("jniortools");
    }

    private static final String URL =  "http://localhost";
    public static void main( String[] args ) throws Exception {

        Coordinate initalPoint = new Coordinate(-3.1878, 55.9444);
        ObstacleService obstacleService = new ObstacleService(URL, "80");
        System.out.println("Obstacle service");
        Map map = new Map(obstacleService);
        SensorService sensorService = new SensorService(URL, "80", "02", "02", "2020");

        ReadingLogger logger1 = new ReadingLogger(initalPoint,"02-02-2021", sensorService.getSensors());
        FlightPathLogger logger2 = new FlightPathLogger(initalPoint,"02-02-2021");
        DroneLogger logger = new CombinedLogger(logger1, logger2);
        Drone drone = new Drone(initalPoint, logger, map, sensorService);
        //System.out.println(map.inAllowedArea(pos));
        //System.out.println(map.inAllowedArea(pos2));
        //System.out.println(map.inAllowedArea(pos3));
        new VisualHelper(map, sensorService);

// the response:
    }
}
