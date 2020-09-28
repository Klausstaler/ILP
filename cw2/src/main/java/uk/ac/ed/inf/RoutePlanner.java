package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.util.HashMap;

/***
 * Computes dist matrix for optimizer.
 */
public class RoutePlanner {

    private GraphOptimizer optimizer;
    private double[][] distanceMatrix;
    private HashMap<Integer, Point> waypoints;

    public RoutePlanner(Map map, Point... waypoints) {
        this.distanceMatrix = new double[waypoints.length][waypoints.length];
        for(int i = 0; i < waypoints.length; i++) {
            this.waypoints.put(i, waypoints[i]);
            for(int j = 0; j < waypoints.length - 1; j++) {

            }
        }
        this.optimizer = new GraphOptimizer(this.distanceMatrix);
    }

}
