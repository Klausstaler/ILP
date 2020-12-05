package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;

import java.util.HashMap;
import java.util.List;

/**
 * Used to create a valid route which visits all waypoints.
 **/
public class RoutePlanner {

    private HashMap<Coordinate, Integer> waypoints = new HashMap<>(); // mapping from a waypoint
    // to coordinate to its index
    private int[] route; // element i gives us the next waypoint index from waypoint i when
    // following the route
    private VisibilityGraph visibilityGraph; // the visibility graph, keeps track of distances
    private PathFinder pathFinder; // used to find shortest path between all pairs

    public RoutePlanner(VisibilityGraph graph, List<Coordinate> waypoints) {

        this.visibilityGraph = graph;

        for (int i = 0; i < waypoints.size(); i++) this.waypoints.put(waypoints.get(i), i);

        System.out.println("Calculating distances and paths for waypoints...");
        var distanceMatrix = this.calculateDistances(waypoints);
        System.out.println("Finished calculating distances and paths for waypoints!");

        System.out.println("NOW OPTIMIZER");
        RouteFinder optimizer = new RouteFinder(distanceMatrix);
        int[] routeIdxs = optimizer.findShortestRoute(); // so far element i stores the
        // waypoint index which we have to reach after reaching the waypoint at the index located
        // at i-1. This makes querying difficult, therefore we transform it in such a way that we
        // can simply ask: we are currently at waypoint index i, what's the next closest
        // waypoint index? by looking at the element located at index i.
        this.route = new int[routeIdxs.length - 1];
        for (int i = 0; i < route.length - 1; i++) {
            this.route[routeIdxs[i]] = routeIdxs[i + 1];
        }
    }

    /**
     * Returns the next path we have to follow in order to follow the optimal route.
     *
     * @param waypoint The current coordinate from which we want to go on.
     * @return A list of Coordinates, the path to reach the next closest waypoint.
     */
    public List<Coordinate> getNextPath(Coordinate waypoint) {
        int waypointIdx = this.waypoints.get(waypoint);
        int offset = this.visibilityGraph.getSize() - this.waypoints.size(); // need to define
        // offset as the pathFinder also stores the paths between the edges of the visibility graph
        return this.pathFinder.getShortestPath(waypointIdx + offset,
                this.route[waypointIdx] + offset);
    }

    /**
     * Calculates the shortest distances between all pairs of waypoints.
     *
     * @param waypoints The list of waypoints.
     * @return A 2D array of doubles, representing the distance matrix. Row index i and column
     * index j is the distance to go from waypoint i to waypoint j.
     */
    private double[][] calculateDistances(List<Coordinate> waypoints) {
        double[][] distanceMatrix = new double[waypoints.size()][waypoints.size()];
        var offset = this.visibilityGraph.getSize(); // offset because we only want the distances
        // between the waypoints
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
