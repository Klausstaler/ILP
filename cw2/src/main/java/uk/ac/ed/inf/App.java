package uk.ac.ed.inf;


import com.mapbox.geojson.Point;
import uk.ac.ed.inf.backend.ObstacleService;

import java.io.IOException;

public class App 
{
    static {
        System.loadLibrary("jniortools");
    }

    private static final String URL =  "http://localhost";
    public static void main( String[] args ) throws IOException {

        Point pos = Point.fromLngLat(-3.1924650818109512,
                55.94621667237433);
        Point pos2 = Point.fromLngLat(-3.1923068314790726,
                55.946166356717846);
        Point pos3 = Point.fromLngLat(-3.192438930273056,
                55.94623957724108);
        //DroneLogger logger = new CombinedLogger(pos, "02-02-2021", ReadingLogger.class,
        //        FlightPathLogger.class);
        ObstacleService obstacleService = new ObstacleService(URL, "80");
        Map map = new Map(obstacleService);
        System.out.println(map.inAllowedArea(pos));
        System.out.println(map.inAllowedArea(pos2));
        System.out.println(map.inAllowedArea(pos3));
        // BackendService service = new SensorService(
        //        "http://localhost", "80", "02", "02", "2021");

// the response:
    }
}
