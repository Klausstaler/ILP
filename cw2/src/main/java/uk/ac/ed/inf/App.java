package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;
import uk.ac.ed.inf.backend.ObstacleService;
import uk.ac.ed.inf.backend.SensorService;

public class App 
{
    private static final String URL =  "http://localhost";
    public static void main( String[] args ) throws Exception {

        String port = "80";
        String day = "02";
        String month = "02";
        String year = "2021";
        String date = String.format("%s-%s-%s", day, month, year);

        Coordinate initalPoint = new Coordinate(-3.1878, 55.9444);
        ObstacleService obstacleService = new ObstacleService(URL, port);
        System.out.println("Obstacle service");
        Map map = new Map(obstacleService.getObstacles());
        SensorService sensorService = new SensorService(URL, port, day, month, year);

        ReadingLogger logger1 = new ReadingLogger(initalPoint,date,
                sensorService.getSensors());
        FlightPathLogger logger2 = new FlightPathLogger(initalPoint,date);
        DroneLogger logger = new CombinedLogger(logger1, logger2);
        new VisualHelper(map, sensorService);
        Drone drone = new Drone(initalPoint, logger, map, sensorService.getSensors());
        //System.out.println(map.inAllowedArea(pos));
        //System.out.println(map.inAllowedArea(pos2));
        //System.out.println(map.inAllowedArea(pos3));

// the response:
    }
}
