package uk.ac.ed.inf;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Point;
import uk.ac.ed.inf.backend.BackendService;
import uk.ac.ed.inf.backend.SensorService;

import java.io.IOException;

public class App 
{
    public static void main( String[] args ) throws IOException {

        Point pos = Point.fromLngLat(55.946233, -3.192473);
        DroneLogger logger = new CombinedLogger(pos, "02-02-2021");

        BackendService service = new SensorService(
                "http://localhost", "80", "02", "02", "2021");

// the response:
    }
}
