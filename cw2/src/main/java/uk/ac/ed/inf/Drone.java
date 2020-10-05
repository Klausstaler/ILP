package uk.ac.ed.inf;


import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import uk.ac.ed.inf.backend.SensorService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Drone {

    private static final int MAX_MOVES = 150;
    private static final double SENSOR_RADIUS = 0.0002;
    private static final double MOVE_LENGTH = 0.0003;

    private Coordinate position;
    private RoutePlanner routePlanner;
    private Map map;
    private List<Sensor> sensors;
    private DroneLogger logger;
    private int numMoves = 0;

    public Drone(Coordinate position, DroneLogger logger, Map map, SensorService sensorService) throws IOException {
        this.position = position;
        this.logger = logger;
        this.sensors = sensorService.getSensors();
        this.map = map;
        List<Coordinate> waypoints = new ArrayList<>();
        waypoints.add(position);
        waypoints.addAll(sensors);
        this.routePlanner = new RoutePlanner(map, waypoints);
        this.visitSensors();


        // Testing
        /*
        List<Point> points = new ArrayList<>();
        points.add(this.toGeoJSON(position));
        List<Coordinate> route = this.routePlanner.getNextRoute(position);
        while (route.get(route.size()-1) != position) {
            for( Coordinate coord : route)
                points.add(this.toGeoJSON(coord));
            Coordinate nextPos = route.get(route.size()-1);
            route = this.routePlanner.getNextRoute(nextPos);
        }
        for( Coordinate coord : route)
            points.add(this.toGeoJSON(coord));
        LineString line = LineString.fromLngLats(points);
        FeatureCollection coll = FeatureCollection.fromFeature(Feature.fromGeometry(line));
        new File("line.geojson").createNewFile();
        FileWriter writer = new FileWriter("line.geojson");
        writer.write(coll.toJson());
        writer.close();

         */
    }

    public void visitSensors() {
        List<Coordinate> route = this.routePlanner.getNextRoute(position);
        Coordinate currCoord = position;
        Coordinate referenceCoord = position;
        while (route.get(route.size()-1) != position) {
            for( Coordinate coord : route) {
                Coordinate newCoord = this.navigate(currCoord, coord);
                this.collectSensorReading();
                // currCoord = newCoord;
                currCoord = coord;
                referenceCoord = coord;
            }
            route = this.routePlanner.getNextRoute(referenceCoord);
        }
    }

    private void collectSensorReading() {
    }

    private Coordinate navigate(Coordinate from, Coordinate to) {
        Coordinate currentCoordinate = from;
        while (currentCoordinate.distance(to) > SENSOR_RADIUS) {
            int angle = this.calculateAngle(currentCoordinate, to);
            double new_x = currentCoordinate.x + Math.cos(angle)*MOVE_LENGTH;
            double new_y = currentCoordinate.y + Math.sin(angle)*MOVE_LENGTH;
            Coordinate newCoordinate = new Coordinate(new_x, new_y);

            Coordinate[] edgeCoords = new Coordinate[]{currentCoordinate, newCoordinate};
            LineString edge = new GeometryFactory().createLineString(edgeCoords);
            if (this.map.inAllowedArea(edge)) {
                
            }
        }
        return null;
    }

    private int calculateAngle(Coordinate coordinate, Coordinate coordinate1) {
        double dx = coordinate1.getX() - coordinate.getX();
        double dy = coordinate1.getY() - coordinate.getY();
        int angle = (int) (Math.round(Math.toDegrees(Math.atan2(dy, dx))) / 10) * 10;

        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

    private Point toGeoJSON(Coordinate coord) {
        return Point.fromLngLat(coord.getX(), coord.getY());
    }
}
