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

        Point pos = Point.fromLngLat(55.946233, -3.192473);
        DroneLogger logger = new CombinedLogger(pos, "02-02-2021", ReadingLogger.class,
                FlightPathLogger.class);

        // BackendService service = new SensorService(
        //        "http://localhost", "80", "02", "02", "2021");

// the response:
    }
}
