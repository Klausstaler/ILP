package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

public class RoutePlanner {

    private GraphOptimizer optimizer;
    private double[][] distanceMatrix;

    public RoutePlanner(Map map, Point... waypoints) {
        this.distanceMatrix = new double[waypoints.length][waypoints.length];
        for(int i = 0; i < waypoints.length; i++) {
            for(int j = 0; j < waypoints.length - 1; j++) {

            }
        }
    }

}
