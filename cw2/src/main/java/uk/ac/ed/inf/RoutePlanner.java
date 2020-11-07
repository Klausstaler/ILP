package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;

import java.util.HashMap;
import java.util.List;

/**
 * Computes dist matrix for optimizer.
 **/
public class RoutePlanner {

    private HashMap<Coordinate, Integer> waypoints = new HashMap<>();
    private int[] route; // element i gives us the next closest waypoint from i
    private VisibilityGraph visibilityGraph;
    private PathFinder pathFinder;

    public RoutePlanner(VisibilityGraph graph, List<Coordinate> waypoints) {

        this.visibilityGraph = graph;
        for (int i = 0; i < waypoints.size(); i++) this.waypoints.put(waypoints.get(i), i);
        System.out.println("Calculating distances and paths for waypoints...");
        var distanceMatrix = this.calculateDistances(waypoints);
        System.out.println("Finished calculating distances and paths for waypoints!");

        System.out.println("NOW OPTIMIZER");
        GraphOptimizer optimizer = new GraphOptimizer(distanceMatrix);
        int[] routeIdxs = optimizer.optimize();
        this.route = new int[routeIdxs.length - 1];
        for (int i = 0; i < route.length - 1; i++) {
            this.route[routeIdxs[i]] = routeIdxs[i + 1];
        }
    }

    public List<Coordinate> getNextPath(Coordinate waypoint) {
        int waypointIdx = this.waypoints.get(waypoint);
        int offset = this.visibilityGraph.getSize() - this.waypoints.size();
        return this.pathFinder.getShortestPath(waypointIdx + offset,
                this.route[waypointIdx] + offset);
    }

    private double[][] calculateDistances(List<Coordinate> waypoints) {
        double[][] distanceMatrix = new double[waypoints.size()][waypoints.size()];
        var offset = this.visibilityGraph.getSize();
        for (var waypoint : waypoints) this.visibilityGraph.addCoordinate(waypoint);
        this.pathFinder = new PathFinder(visibilityGraph);

        for (int i = 0; i < waypoints.size(); i++) {
            for (int j = i + 1; j < waypoints.size(); j++) {
                int fromIdx = offset + i;
                int toIdx = offset + j;
                double distance = this.visibilityGraph.getDistance(fromIdx, toIdx);
                distanceMatrix[i][j] = distance;
                distanceMatrix[j][i] = distance;
            }
        }
        return distanceMatrix;
    }
}
