package uk.ac.ed.inf;


import com.mapbox.geojson.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import uk.ac.ed.inf.backend.ObstacleService;
import uk.ac.ed.inf.backend.SensorService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App 
{
    static {
        System.loadLibrary("jniortools");
    }

    private static final String URL =  "http://localhost";
    public static void main( String[] args ) throws IOException, IllegalAccessException,
            InstantiationException, InvocationTargetException {

        Point pos = Point.fromLngLat(-3.1924650818109512,
                55.94621667237433);
        Point pos2 = Point.fromLngLat(-3.1923068314790726,
                55.946166356717846);
        Point pos3 = Point.fromLngLat(-3.192438930273056,
                55.94623957724108);
        GeometryFactory fac = new GeometryFactory();
        org.locationtech.jts.geom.Point initalPoint = fac.createPoint(new Coordinate(pos.longitude(),
                pos.latitude()));
        ReadingLogger logger1 = new ReadingLogger(initalPoint,"02-02-2021");
        FlightPathLogger logger2 = new FlightPathLogger(initalPoint,"02-02-2021");
        DroneLogger logger = new CombinedLogger(logger1, logger2);
        ObstacleService obstacleService = new ObstacleService(URL, "80");
        System.out.println("Obstacle service");
        Map map = new Map(obstacleService);
        SensorService sensorService = new SensorService(URL, "80", "02", "02", "2020");
        Drone drone = new Drone(logger, map, sensorService);
        //System.out.println(map.inAllowedArea(pos));
        //System.out.println(map.inAllowedArea(pos2));
        //System.out.println(map.inAllowedArea(pos3));
        new VisualHelper(map, sensorService);

// the response:
    }
}
