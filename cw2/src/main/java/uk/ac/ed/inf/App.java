package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;
import uk.ac.ed.inf.backend.ObstacleService;
import uk.ac.ed.inf.backend.SensorService;

public class App
{
    private static final String URL =  "http://localhost";
    public static void main( String[] args ) throws Exception {

        String port = "80";
        String[] dd_mm = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
                "11", "12"};
        //String day = "02";
        //String month = "02";
        //String year = "2021";
        for(String day : dd_mm) {
            String month = day;
            String year = "2020";
            String date = String.format("%s-%s-%s", day, month, year);
            System.out.println("CHECKING " + date);
            Coordinate initalPoint = new Coordinate( -3.188396, 55.944425);
            ObstacleService obstacleService = new ObstacleService(URL, port);
            System.out.println("Obstacle service");
            Map map = new Map(obstacleService.getObstacles());
            SensorService sensorService = new SensorService(URL, port, day, month, year);

            ReadingLogger logger1 = new ReadingLogger(initalPoint,date,
                    sensorService.getSensors());
            FlightPathLogger logger2 = new FlightPathLogger(initalPoint,date);
            new VisualHelper(map, sensorService);
            Drone drone = new Drone(initalPoint, map, sensorService.getSensors(), logger1, logger2);
            year = "2021";
            date = String.format("%s-%s-%s", day, month, year);
            System.out.println("CHECKING " + date);
            sensorService = new SensorService(URL, port, day, month, year);

            logger1 = new ReadingLogger(initalPoint,date,
                    sensorService.getSensors());
            logger2 = new FlightPathLogger(initalPoint,date);
            new VisualHelper(map, sensorService);
            drone = new Drone(initalPoint, map, sensorService.getSensors(), logger1, logger2);
        }

// the response:
    }
}
