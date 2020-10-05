package uk.ac.ed.inf;


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
    private HashMap<Coordinate, Integer> waypoints = new HashMap<>();
    private int[] route;
    private Map map;
    private VisibilityGraph visibilityGraph;
    private List<List<List<Coordinate>>> paths = new ArrayList<>();

    public RoutePlanner(Map map, List<Coordinate> waypoints) throws IOException {
        this.distanceMatrix = new double[waypoints.size()][waypoints.size()];
        this.map = map;
        this.visibilityGraph = new VisibilityGraph(this.map.getPlayArea());

        System.out.println("Calculating distances and paths for waypoints...");
        for(int i = 0; i < waypoints.size(); i++) {;
            this.waypoints.put(waypoints.get(i), i);
            List<List<Coordinate>> row = new ArrayList<>();
            for(int j = 0; j < waypoints.size(); j++) {
                double distance = 0.0;
                row.add(new ArrayList<>());
                if (i != j) {
                    Pair<List<Coordinate>, Double> pathInfo =
                            this.calculateDistance(waypoints.get(i),
                                    waypoints.get(j));
                    distance = pathInfo.second;
                    row.set(j, pathInfo.first);
                }
                this.distanceMatrix[i][j] = distance;
            }
            paths.add(row);
        }
        System.out.println("Finished calculating distances and paths for waypoints!");
        /*
        for(double[] row : distanceMatrix) {
            System.out.println(Arrays.toString(row));
        }

         */
        System.out.println("NOW OPTIMIZER");
        this.optimizer = new GraphOptimizer(this.distanceMatrix);
        int[] routeIdxs = this.optimizer.optimize();
        this.route = new int[routeIdxs.length-1];
        for(int i = 0; i < route.length - 1; i++) {
            this.route[routeIdxs[i]] = routeIdxs[i + 1];
        }
    }

    private Pair<List<Coordinate>, Double> calculateDistance(Coordinate waypoint, Coordinate waypoint1) {
        List<Coordinate> path = new ArrayList<>();
        Coordinate[] coordinates = new Coordinate[] {waypoint, waypoint1};
        LineString line = new GeometryFactory().createLineString(coordinates);
        double dist = line.getLength();
        if (!this.map.inAllowedArea(line)) {
            this.visibilityGraph.addCoordinate(waypoint);
            this.visibilityGraph.addCoordinate(waypoint1);
            PathFinder pathFinder = new PathFinder(this.visibilityGraph.getGraph());
            Pair<int[], Double> pair = pathFinder.shortestPath(pathFinder.getNumNodes()-2,
                    pathFinder.getNumNodes()-1);
            dist = pair.second;

            //System.out.println("SHORTEST PATH FROM " + waypoint + "TO " + waypoint1);
            for(int i = 1; i < pair.first.length-1; i++) {
                //System.out.println(this.visibilityGraph.getAllCoordinates().get(pair.first[i]));
                path.add(this.visibilityGraph.getAllCoordinates().get(pair.first[i]));
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
