package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import org.locationtech.jts.geom.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * Computes dist matrix for optimizer.
 */
public class RoutePlanner {

    private GraphOptimizer optimizer;
    private double[][] distanceMatrix;
    private HashMap<Integer, Point> waypoints = new HashMap<>();
    private Map map;
    private VisibilityGraph visibilityGraph;
    private HashMap<Integer, List<Coordinate>> paths = new HashMap<>();

    private List<Feature> features = new ArrayList<>();

    public RoutePlanner(Map map, Point... waypoints) throws IOException {
        this.distanceMatrix = new double[waypoints.length][waypoints.length];
        this.map = map;
        this.visibilityGraph = new VisibilityGraph(this.map.getPlayArea());
        for(int i = 0; i < waypoints.length; i++) {
            this.waypoints.put(i, waypoints[i]);
            for(int j = 0; j < waypoints.length - 1; j++) {
                double distance = 0.0;
                if (i != j)
                    distance = this.calculateDistance(waypoints[i], waypoints[j]);
                this.distanceMatrix[i][j] = distance;
                this.distanceMatrix[j][i] = distance;
            }
        }
        this.optimizer = new GraphOptimizer(this.distanceMatrix);
    }

    private double calculateDistance(Point waypoint, Point waypoint1) {

        Coordinate from = new Coordinate(waypoint.longitude(), waypoint.latitude());
        Coordinate to = new Coordinate(waypoint1.longitude(), waypoint1.latitude());
        Coordinate[] coordinates = new Coordinate[] {from, to};
        LineString line = new GeometryFactory().createLineString(coordinates);
        double dist = line.getLength();
        if (!this.map.inAllowedArea(line)) {
            this.visibilityGraph.addCoordinate(new Coordinate(waypoint.longitude(), waypoint.latitude()));
            this.visibilityGraph.addCoordinate(new Coordinate(waypoint1.longitude(), waypoint1.latitude()));
            PathFinder pathFinder = new PathFinder(this.visibilityGraph.getGraph());
            int[] path = pathFinder.shortestPath(pathFinder.getNumNodes()-2,
                    pathFinder.getNumNodes()-1);

            System.out.println("SHORTEST PATH FROM " + waypoint + "TO " + waypoint1);
            for(int i = 1; i < path.length-1; i++)
                System.out.println(this.visibilityGraph.getAllCoordinates().get(path[i]));
            this.visibilityGraph.removeLast();
            this.visibilityGraph.removeLast();
            dist = (double) Long.MAX_VALUE / 2;
        }
        return dist;
    }
}
