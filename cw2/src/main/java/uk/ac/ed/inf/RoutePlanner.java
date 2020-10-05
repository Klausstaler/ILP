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

    private GraphOptimizer optimizer;
    private double[][] distanceMatrix;
    private HashMap<Point, Integer> waypoints = new HashMap<>();
    private HashMap<Integer, Integer> route = new HashMap<>(); // replace by simple list
    private Map map;
    private VisibilityGraph visibilityGraph;
    private List<List<List<Coordinate>>> paths = new ArrayList<>();

    public RoutePlanner(Map map, List<Point> waypoints) throws IOException {
        this.distanceMatrix = new double[waypoints.size()][waypoints.size()];
        this.map = map;
        this.visibilityGraph = new VisibilityGraph(this.map.getPlayArea());

        for(int i = 0; i < waypoints.size(); i++) {
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
        /*
        for(double[] row : distanceMatrix) {
            System.out.println(Arrays.toString(row));
        }

         */
        System.out.println("NOW OPTIMIZER");
        this.optimizer = new GraphOptimizer(this.distanceMatrix);
        int[] route = this.optimizer.optimize();
        for(int i = 0; i < route.length -1; i++)
            this.route.put(route[i], route[i+1]);
        System.out.println("NUM waypoints" + paths.size() + "NUM elements" + paths.get(0).size());
    }

    private Pair<List<Coordinate>, Double> calculateDistance(Point waypoint, Point waypoint1) {
        List<Coordinate> path = new ArrayList<>();
        Coordinate[] coordinates = new Coordinate[] {waypoint.getCoordinate(), waypoint1.getCoordinate()};
        LineString line = new GeometryFactory().createLineString(coordinates);
        double dist = line.getLength();
        if (!this.map.inAllowedArea(line)) {
            this.visibilityGraph.addCoordinate(waypoint.getCoordinate());
            this.visibilityGraph.addCoordinate(waypoint1.getCoordinate());
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
        path.add(waypoint1.getCoordinate());
        return new Pair<>(path, dist);
    }

    public List<Coordinate> getNextRoute(Point waypoint) {
        int waypointIdx = this.waypoints.get(waypoint);
        //System.out.println("ROUTEPLANNER WAYPOINT IDX " + waypointIdx);
        return this.paths.get(waypointIdx).get(this.route.get(waypointIdx));
    }
}
