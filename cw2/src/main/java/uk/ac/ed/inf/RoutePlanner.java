package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Computes dist matrix for optimizer.
 **/
public class RoutePlanner {

    private HashMap<Coordinate, Integer> waypoints = new HashMap<>();
    private int[] route; // element i gives us the next closest waypoint from i
    private Map map;
    private VisibilityGraph visibilityGraph;
    private List<List<List<Coordinate>>> paths = new ArrayList<>();

    public RoutePlanner(Map map, List<Coordinate> waypoints) {
        double[][] distanceMatrix = new double[waypoints.size()][waypoints.size()];
        this.map = map;
        this.visibilityGraph = new VisibilityGraph(this.map);

        for(int i = 0; i < waypoints.size(); i++) {
            this.waypoints.put(waypoints.get(i), i);
            List<List<Coordinate>> paths = new ArrayList<>(); // all paths from i to all other
            // waypoints
            for (int j = 0; j < waypoints.size(); j++) paths.add(new ArrayList<>());
            this.paths.add(paths);
        }
        
        System.out.println("Calculating distances and paths for waypoints...");
        for (int i = 0; i < waypoints.size(); i++) {
            for (int j = i+1; j < waypoints.size(); j++) {
                Coordinate from = waypoints.get(i);
                Coordinate to = waypoints.get(j);
                Pair<List<Coordinate>, Double> pathInfo =
                        this.calculateDistance(from, to);
                this.updatePaths(from, to, pathInfo.first);
                double distance = pathInfo.second;
                distanceMatrix[i][j] = distance;
                distanceMatrix[j][i] = distance;
            }
        }
        System.out.println("Finished calculating distances and paths for waypoints!");

        System.out.println("NOW OPTIMIZER");
        GraphOptimizer optimizer = new GraphOptimizer(distanceMatrix);
        int[] routeIdxs = optimizer.optimize();
        this.route = new int[routeIdxs.length - 1];
        for (int i = 0; i < route.length - 1; i++) {
            this.route[routeIdxs[i]] = routeIdxs[i + 1];
        }
    }

    private void updatePaths(Coordinate from, Coordinate to, List<Coordinate> path) {
        int from_idx = this.waypoints.get(from);
        int to_idx = this.waypoints.get(to);
        paths.get(from_idx).set(to_idx, path); // path from i to j with possible
        // waypoints in between
        List<Coordinate> reversedPath = new ArrayList<>(path);
        reversedPath.remove(reversedPath.size()-1); // remove 'to' coordinate, as we now go from
        // need 'from' at the end
        Collections.reverse(reversedPath);
        reversedPath.add(from);
        paths.get(to_idx).set(from_idx, reversedPath);
    }

    private Pair<List<Coordinate>, Double> calculateDistance(Coordinate from, Coordinate to) {
        double dist = from.distance(to);

        List<Coordinate> path = new ArrayList<>();
        if (!this.map.verifyMove(from, to)) {
            this.visibilityGraph.addCoordinate(from);
            this.visibilityGraph.addCoordinate(to);
            PathFinder pathFinder = new PathFinder(this.visibilityGraph.getDistances());
            Pair<int[], Double> pair = pathFinder.shortestPath(pathFinder.getNumNodes() - 2,
                    pathFinder.getNumNodes() - 1);
            int[] routeIdxs = pair.first;
            dist = pair.second;

            for (int i = 1; i < routeIdxs.length; i++) {
                path.add(this.visibilityGraph.getAllCoordinates().get(routeIdxs[i]));
            }
            this.visibilityGraph.removeLast();
            this.visibilityGraph.removeLast();
        }
        else path.add(to);
        return new Pair<>(path, dist);
    }

    public List<Coordinate> getNextRoute(Coordinate waypoint) {
        int waypointIdx = this.waypoints.get(waypoint);
        return this.paths.get(waypointIdx).get(this.route[waypointIdx]);
    }
}
