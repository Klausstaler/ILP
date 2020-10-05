package uk.ac.ed.inf;


import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import uk.ac.ed.inf.backend.SensorService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Drone {

    private Point position;
    private RoutePlanner routePlanner;
    private List<Sensor> sensors;
    private DroneLogger logger;

    public Drone(Point position, DroneLogger logger, Map map, SensorService sensorService) throws IOException {
        this.position = position;
        this.logger = logger;
        this.sensors = sensorService.getSensors();
        List<Point> waypoints = new ArrayList<>();
        waypoints.add(position);
        waypoints.addAll(sensors);
        this.routePlanner = new RoutePlanner(map, waypoints);

        // Testing
        HashMap<Coordinate, Point> test = new HashMap<>();
        for (Point waypoint : waypoints)
            test.put(waypoint.getCoordinate(), waypoint);
        List<com.mapbox.geojson.Point> points = new ArrayList<>();
        points.add(this.toGeoJSON(position.getCoordinate()));
        List<Coordinate> route = this.routePlanner.getNextRoute(position);
        while (route.get(route.size()-1) != position.getCoordinate()) {
            for( Coordinate coord : route)
                points.add(this.toGeoJSON(coord));
            Coordinate nextPos = route.get(route.size()-1);
            route = this.routePlanner.getNextRoute(test.get(nextPos));
        }
        for( Coordinate coord : route)
            points.add(this.toGeoJSON(coord));
        LineString line = LineString.fromLngLats(points);
        FeatureCollection coll = FeatureCollection.fromFeature(Feature.fromGeometry(line));
        new File("line.geojson").createNewFile();
        FileWriter writer = new FileWriter("line.geojson");
        writer.write(coll.toJson());
        writer.close();
    }

    public void navigate() {
    }

    private com.mapbox.geojson.Point toGeoJSON(Coordinate coord) {
        return com.mapbox.geojson.Point.fromLngLat(coord.getX(), coord.getY());
    }
}
