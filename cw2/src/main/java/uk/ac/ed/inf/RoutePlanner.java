package uk.ac.ed.inf;


import org.locationtech.jts.geom.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/***
 * Computes dist matrix for optimizer.
 */
public class RoutePlanner {

    private HashMap<Coordinate, Integer> waypoints = new HashMap<>();
    private int[] route; // element i gives us the next closest waypoint from i
    private Map map;
    private VisibilityGraph visibilityGraph;
    private List<List<List<Coordinate>>> paths = new ArrayList<>();

    public RoutePlanner(Map map, List<Coordinate> waypoints) throws IOException {
        double[][] distanceMatrix = new double[waypoints.size()][waypoints.size()];
        this.map = map;
        this.visibilityGraph = new VisibilityGraph(this.map.getPlayArea());

        System.out.println("Calculating distances and paths for waypoints...");
        for(int i = 0; i < waypoints.size(); i++) {;
            this.waypoints.put(waypoints.get(i), i);
            List<List<Coordinate>> paths = new ArrayList<>(); // all paths from i to all other
            // waypoints
            for(int j = 0; j < waypoints.size(); j++) {
                double distance = 0.0;
                paths.add(new ArrayList<>());
                if (i != j) {
                    Pair<List<Coordinate>, Double> pathInfo =
                            this.calculateDistance(waypoints.get(i),
                                    waypoints.get(j));
                    paths.set(j, pathInfo.first); // path from i to j with possible waypoints in
                    // between
                    distance = pathInfo.second;
                }
                distanceMatrix[i][j] = distance;
            }
            this.paths.add(paths);
        }
        System.out.println("Finished calculating distances and paths for waypoints!");

        System.out.println("NOW OPTIMIZER");
        GraphOptimizer optimizer = new GraphOptimizer(distanceMatrix);
        int[] routeIdxs = optimizer.optimize();
        this.route = new int[routeIdxs.length-1];
        for(int i = 0; i < route.length - 1; i++) {
            this.route[routeIdxs[i]] = routeIdxs[i + 1];
        }
    }

    private Pair<List<Coordinate>, Double> calculateDistance(Coordinate waypoint, Coordinate waypoint1) {
        Coordinate[] coordinates = new Coordinate[] {waypoint, waypoint1};
        LineString line = new GeometryFactory().createLineString(coordinates);
        double dist = line.getLength();

        List<Coordinate> path = new ArrayList<>();
        if (!this.map.inAllowedArea(line)) {
            this.visibilityGraph.addCoordinate(waypoint);
            this.visibilityGraph.addCoordinate(waypoint1);
            PathFinder pathFinder = new PathFinder(this.visibilityGraph.getGraph());
            Pair<int[], Double> pair = pathFinder.shortestPath(pathFinder.getNumNodes()-2,
                    pathFinder.getNumNodes()-1);
            int[] routeIdxs = pair.first;
            dist = pair.second;

            for(int i = 1; i < routeIdxs.length-1; i++) {
                path.add(this.visibilityGraph.getAllCoordinates().get(routeIdxs[i]));
            }
            this.visibilityGraph.removeLast();
            this.visibilityGraph.removeLast();
        }
        path.add(waypoint1);
        return new Pair<>(path, dist);
    }

    public List<Coordinate> getNextRoute(Coordinate waypoint) {
        int waypointIdx = this.waypoints.get(waypoint);
        return this.paths.get(waypointIdx).get(this.route[waypointIdx]);
    }
}
