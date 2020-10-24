package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;
import uk.ac.ed.inf.backend.ObstacleService;
import uk.ac.ed.inf.backend.SensorService;

public class App {
    private static final String URL = "http://localhost";

    public static void main(String[] args) throws Exception {

        String port = "80";
        String[] dd_mm = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
                "11", "12"};

        /*
        for (String day : dd_mm) {
            for (String year : new String[]{"2020", "2021"}) {
                String month = day;
                String date = String.format("%s-%s-%s", day, month, year);
                System.out.println("CHECKING " + date);
                //Coordinate initalPoint = new Coordinate(-3.188396, 55.944425);
                Coordinate initalPoint = new Coordinate(-3.186922967433929,
                        55.94487577533465);
                ObstacleService obstacleService = new ObstacleService(URL, port);
                System.out.println("Obstacle service");
                Map map = new Map(obstacleService.getObstacles());
                SensorService sensorService = new SensorService(URL, port, day, month, year);

                ReadingLogger logger1 = new ReadingLogger(initalPoint, date,
                        sensorService.getSensors());
                FlightPathLogger logger2 = new FlightPathLogger(initalPoint, date);
                new VisualHelper(map, sensorService);
                Drone drone = new Drone(initalPoint, map, sensorService.getSensors(), logger1,
                        logger2);
            }
        }
        */
        for (int d = 29; d < 31; d++) {
            for (int m = 3; m < 13; m++) {
                for (String year : new String[]{"2020", "2021"}) {
                    String month = String.valueOf(m);
                    month = month.length() < 2 ? "0" + month : month;
                    String day = String.valueOf(d);
                    day = day.length() < 2 ? "0" + day : day;
                    String date = String.format("%s-%s-%s", day, month, year);
                    System.out.println("CHECKING " + date);
                    Coordinate initalPoint = new Coordinate(-3.186922967433929,
                            55.94487577533465);
                    ObstacleService obstacleService = new ObstacleService(URL, port);
                    System.out.println("Obstacle service");
                    Map map = new Map(obstacleService.getObstacles());
                    SensorService sensorService = new SensorService(URL, port, day, month, year);

                    ReadingLogger logger1 = new ReadingLogger(initalPoint, date,
                            sensorService.getSensors());
                    FlightPathLogger logger2 = new FlightPathLogger(initalPoint, date);
                    new VisualHelper(map, sensorService);
                    Drone drone = new Drone(initalPoint, map, sensorService.getSensors(), logger1
                            , logger2);
                }
            }
        }

    }
}
