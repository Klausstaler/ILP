package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;
import uk.ac.ed.inf.backend.ObstacleService;
import uk.ac.ed.inf.backend.SensorService;

import java.util.Random;

public class App {
    private static final String URL = "http://localhost";
    private static Random random;

    public static void main(String[] args) throws Exception {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "Error"); //
        // disables logging for graphhopper API

        /*
        String day = args[0];
        String month = args[1];
        String year = args[2];
        double latitude = Double.parseDouble(args[3]);
        double longitude = Double.parseDouble(args[4]);
        random = new Random(Integer.parseInt(args[5]));
        String port = args[6];
        */
        /*
        var day = "15";
        var month = "01";
        var year = "2020";
        var port = "80";
        random = new Random(5678);
        String date = String.format("%s-%s-%s", day, month, year);
         */

        /*
        ObstacleService obstacleService = new ObstacleService(URL, port);

        Map map = new Map(obstacleService.getObstacles());

        SensorService sensorService = new SensorService(URL, port, day, month, year);

        new VisualHelper(map, sensorService);

        Coordinate initialPoint = new Coordinate(longitude, latitude);
        //Coordinate initialPoint = new Coordinate(-3.188396, 55.944425);
        var logger1 = new ReadingLogger(initialPoint, date, sensorService.getSensors());
        var logger2 = new FlightPathLogger(initialPoint, date);

        Drone drone = new Drone(initialPoint, map, sensorService.getSensors(), logger1,
                logger2);

        drone.visitSensors();
         */

        String port = "80";
        String[] dd_mm = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
                "11", "12"};
        random = new Random(5678);

        for (int d = 1; d < 31; d++) {
            for (int m = 1; m < 13; m++) {
                for (String year : new String[]{"2020", "2021"}) {
                    if (m == 2 && d > 28) continue;
                    if (m % 2 == 0 && d > 30) continue;
                    String month = String.valueOf(m);
                    month = month.length() < 2 ? "0" + month : month;
                    String day = String.valueOf(d);
                    day = day.length() < 2 ? "0" + day : day;


                    String date = String.format("%s-%s-%s", day, month, year);
                    System.out.println("CHECKING " + date);
                    //Coordinate initialPoint = new Coordinate(-3.188396, 55.944425);
                    Coordinate initialPoint = new Coordinate(-3.186924308538437,55.9449287211836);
                    ObstacleService obstacleService = new ObstacleService(URL, port);
                    System.out.println("Obstacle service");
                    Map map = new Map(obstacleService.getObstacles());
                    SensorService sensorService = new SensorService(URL, port, day, month, year);

                    ReadingLogger logger1 = new ReadingLogger(initialPoint, date,
                            sensorService.getSensors());
                    FlightPathLogger logger2 = new FlightPathLogger(initialPoint, date);
                    new VisualHelper(map, sensorService);
                    Drone drone = new Drone(initialPoint, map, sensorService.getSensors(), logger1
                            , logger2);
                    drone.visitSensors();
                    //break;
                }
                //break;
            }
            //break;
        }
         //*/

        /*
        for (String day : dd_mm) {
            for (String year : new String[]{"2020", "2021"}) {
                String date = String.format("%s-%s-%s", day, day, year);
                System.out.println("CHECKING " + date);

                ObstacleService obstacleService = new ObstacleService(URL, port);
                Map map = new Map(obstacleService.getObstacles());

                SensorService sensorService = new SensorService(URL, port, day, day, year);
                Coordinate initialPoint = new Coordinate(-3.188396, 55.944425);

                new VisualHelper(map, sensorService);

                ReadingLogger logger1 = new ReadingLogger(initialPoint, date,
                        sensorService.getSensors());
                FlightPathLogger logger2 = new FlightPathLogger(initialPoint, date);
                Drone drone = new Drone(initialPoint, map, sensorService.getSensors(), logger1,
                        logger2);

                drone.visitSensors();
            }
        }
        */

    }

    public static Random getRandom() {
        return random;
    }
}
