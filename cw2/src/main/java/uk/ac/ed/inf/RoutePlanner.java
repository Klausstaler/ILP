package uk.ac.ed.inf;


import org.locationtech.jts.geom.*;

import java.io.IOException;
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

    public RoutePlanner(Map map, List<Point> waypoints) throws IOException {
        this.distanceMatrix = new double[waypoints.size()][waypoints.size()];
        this.map = map;
        this.visibilityGraph = new VisibilityGraph(this.map.getPlayArea());
        for(int i = 0; i < waypoints.size(); i++) {
            this.waypoints.put(i, waypoints.get(i));
            for(int j = 0; j < waypoints.size() - 1; j++) {
                double distance = 0.0;
                if (i != j)
                    distance = this.calculateDistance(waypoints.get(i), waypoints.get(j));
                this.distanceMatrix[i][j] = distance;
                this.distanceMatrix[j][i] = distance;
            }
        }
        this.optimizer = new GraphOptimizer(this.distanceMatrix);
    }

    private double calculateDistance(Point waypoint, Point waypoint1) {

        Coordinate[] coordinates = new Coordinate[] {waypoint.getCoordinate(), waypoint1.getCoordinate()};
        LineString line = new GeometryFactory().createLineString(coordinates);
        double dist = line.getLength();
        if (!this.map.inAllowedArea(line)) {
            this.visibilityGraph.addCoordinate(waypoint.getCoordinate());
            this.visibilityGraph.addCoordinate(waypoint1.getCoordinate());
            PathFinder pathFinder = new PathFinder(this.visibilityGraph.getGraph());
            Pair<int[], Double> path_dist = pathFinder.shortestPath(pathFinder.getNumNodes()-2,
                    pathFinder.getNumNodes()-1);

            System.out.println("SHORTEST PATH FROM " + waypoint + "TO " + waypoint1);
            for(int i = 1; i < path_dist.first.length-1; i++) {
                System.out.println(this.visibilityGraph.getAllCoordinates().get(path_dist.first[i]));
            }
            this.visibilityGraph.removeLast();
            this.visibilityGraph.removeLast();
            dist = (double) Long.MAX_VALUE / 2;
        }
        return dist;
    }
}
