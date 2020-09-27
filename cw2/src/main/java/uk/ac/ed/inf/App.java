package uk.ac.ed.inf;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Point;
import org.locationtech.jts.geom.GeometryFactory;
import uk.ac.ed.inf.backend.BackendService;
import uk.ac.ed.inf.backend.ObstacleService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App 
{
    public static void main( String[] args ) throws IllegalAccessException,
            InstantiationException, InvocationTargetException, IOException {

        Point pos = Point.fromLngLat(-3.1924650818109512,
                55.94621667237433);
        Point pos2 = Point.fromLngLat(-3.1923068314790726,
                55.946166356717846);
        Point pos3 = Point.fromLngLat(-3.192438930273056,
                55.94623957724108);
        //DroneLogger logger = new CombinedLogger(pos, "02-02-2021", ReadingLogger.class,
        //        FlightPathLogger.class);
        Map map = Map.getInstance();
        System.out.println(map.inAllowedArea(pos));
        System.out.println(map.inAllowedArea(pos2));
        System.out.println(map.inAllowedArea(pos3));
        // BackendService service = new SensorService(
        //        "http://localhost", "80", "02", "02", "2021");
        BackendService obstacleService = new ObstacleService("http://localhost", "80");

// the response:
    }
}
