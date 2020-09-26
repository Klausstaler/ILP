package uk.ac.ed.inf;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Point;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App 
{
    public static void main( String[] args ) throws IllegalAccessException,
            InstantiationException, InvocationTargetException {

        Point pos = Point.fromLngLat(-3.192471, 55.946229);
        Point pos2 = Point.fromLngLat(-3.1924670934677124,
                55.94625365809505);
        Point pos3 = Point.fromLngLat(-3.1924670934677124,
                55.94625365809505);
        DroneLogger logger = new CombinedLogger(pos, "02-02-2021", ReadingLogger.class,
                FlightPathLogger.class);
        Map map = Map.getInstance();
        System.out.println(map.inAllowedArea(pos));
        System.out.println(map.inAllowedArea(pos2));
        System.out.println(map.inAllowedArea(pos3));
        // BackendService service = new SensorService(
        //        "http://localhost", "80", "02", "02", "2021");

// the response:
    }
}
