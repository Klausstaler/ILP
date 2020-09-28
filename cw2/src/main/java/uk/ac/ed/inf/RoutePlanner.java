package uk.ac.ed.inf;

import com.mapbox.geojson.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import java.util.Arrays;
import java.util.HashMap;

/***
 * Computes dist matrix for optimizer.
 */
public class RoutePlanner {

    private GraphOptimizer optimizer;
    private double[][] distanceMatrix;
    private HashMap<Integer, Point> waypoints = new HashMap<>();
    private Map map;

    public RoutePlanner(Map map, Point... waypoints) {
        this.distanceMatrix = new double[waypoints.length][waypoints.length];
        this.map = map;
        for(int i = 0; i < waypoints.length; i++) {
            this.waypoints.put(i, waypoints[i]);
            for(int j = 0; j < waypoints.length - 1; j++) {
                double distance = 0.0;
                if (i != j)
                    distance = this.calculateDistance(waypoints[i], waypoints[j]);
                this.distanceMatrix[i][j] = distance;
                this.distanceMatrix[j][i] = distance;
            }
            System.out.println(Arrays.toString(this.distanceMatrix[i]));
        }
        this.optimizer = new GraphOptimizer(this.distanceMatrix);
    }

    private double calculateDistance(Point waypoint, Point waypoint1) {
        double dist = Math.sqrt(Math.pow(waypoint.longitude() - waypoint1.longitude(), 2) +
                Math.pow(waypoint.latitude() - waypoint1.latitude(), 2));
        Coordinate coordinate = new Coordinate(waypoint.longitude(), waypoint.latitude());
        Coordinate coordinate1 = new Coordinate(waypoint1.longitude(), waypoint1.latitude());
        Coordinate[] coordinates = new Coordinate[] {coordinate, coordinate1};
        LineString line = new GeometryFactory().createLineString(coordinates);
        if (!this.map.getPlayArea().covers(line))
            dist = (double) Long.MAX_VALUE / 2;
        return dist;
    }
}
