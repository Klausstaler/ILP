package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import org.locationtech.jts.geom.*;

import java.io.File;
import java.io.FileWriter;
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
    private HashMap<Integer, Point> waypoints = new HashMap<>();
    private Map map;
    private double[][] visibilityGraph;

    private List<Feature> features = new ArrayList<>();

    public RoutePlanner(Map map, Point... waypoints) throws IOException {
        this.distanceMatrix = new double[waypoints.length][waypoints.length];
        this.map = map;
        this.visibilityGraph = this.constructVisibilityGraph();
        for(int i = 0; i < waypoints.length; i++) {
            this.waypoints.put(i, waypoints[i]);
            for(int j = 0; j < waypoints.length - 1; j++) {
                double distance = 0.0;
                if (i != j)
                    distance = this.calculateDistance(waypoints[i], waypoints[j]);
                this.distanceMatrix[i][j] = distance;
                this.distanceMatrix[j][i] = distance;
            }
            //System.out.println(Arrays.toString(this.distanceMatrix[i]));
        }
        this.optimizer = new GraphOptimizer(this.distanceMatrix);
    }

    private double[][] constructVisibilityGraph() throws IOException {
        Geometry mapShape = this.map.getPlayArea().getBoundary();
        List<Geometry> boundaries = new ArrayList<>();
        for(int i = 0; i < mapShape.getNumGeometries(); i++)
            boundaries.add(mapShape.getGeometryN(i));


        List<Coordinate> allCoordinates = new ArrayList<>();
        for(Geometry obstacle: boundaries) {
            allCoordinates.addAll(Arrays.asList(obstacle.getCoordinates()));
        }


        double[][] visibilityGraph = new double[allCoordinates.size()][allCoordinates.size()];
        for(double[] row: visibilityGraph)
            Arrays.fill(row, (double) Long.MAX_VALUE / 2);
        int offset = 0;
        for (Geometry obstacle: boundaries) {
            Coordinate[] coordinates = obstacle.getCoordinates();
            for(int idx = 0; idx < coordinates.length-1; idx++) {
                int pos = idx + offset;
                visibilityGraph[pos][pos+1] = coordinates[idx].distance(coordinates[idx+1]);
                visibilityGraph[pos+1][pos] = coordinates[idx].distance(coordinates[idx+1]);
            }
            offset += coordinates.length;
        }

        new File("graphviz.geojson").createNewFile();
        FileWriter writer = new FileWriter("graphviz.geojson");

        double EPSILON = 0.001;
        GeometryFactory factory = new GeometryFactory();
        for(int i = 0; i < allCoordinates.size(); i++) {
            for(int j = 0; j < allCoordinates.size(); j++) {
                if (i == j)
                    continue;
                Coordinate from = allCoordinates.get(i).copy();
                Coordinate to = allCoordinates.get(j).copy();
                double diff_x = Math.abs(from.x - to.x);
                double diff_y = Math.abs(from.y - to.y);
                from.x += from.x < to.x ? diff_x*EPSILON : -diff_x*EPSILON;
                to.x += from.x > to.x ? diff_x*EPSILON : -diff_x*EPSILON;
                from.y += from.y < to.y ? diff_y*EPSILON : -diff_y*EPSILON;
                to.y += to.y < from.y ? diff_y*EPSILON : -diff_y*EPSILON;
                Coordinate[] edgeCoords = new Coordinate[] {from, to};
                LineString edge = factory.createLineString(edgeCoords);
                if (this.map.getPlayArea().covers(edge)) {
                    System.out.println("YEET EDGE FROM " + from + "TO " + to);
                    Point point1  = Point.fromLngLat(from.x, from.y);
                    Point point2 = Point.fromLngLat(to.x, to.y);
                    ArrayList<Point> test = new ArrayList<>();
                    test.add(point1);
                    test.add(point2);
                    com.mapbox.geojson.LineString lineString =
                            com.mapbox.geojson.LineString.fromLngLats(test);
                    features.add(Feature.fromGeometry(lineString));
                    visibilityGraph[i][j] = edge.getLength();
                }
                else {
                    System.out.println("NO EDGE FROM " + from + "TO " + to);
                }
            }
        }

        String res =  FeatureCollection.fromFeatures(features).toJson();
        //System.out.println(res);
        writer.write(res);
        writer.close();

        for(double[] row : visibilityGraph) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println(visibilityGraph.length);
        return null;
    }

    private double calculateDistance(Point waypoint, Point waypoint1) {
        double dist = Math.sqrt(Math.pow(waypoint.longitude() - waypoint1.longitude(), 2) +
                Math.pow(waypoint.latitude() - waypoint1.latitude(), 2));
        Coordinate coordinate = new Coordinate(waypoint.longitude(), waypoint.latitude());
        Coordinate coordinate1 = new Coordinate(waypoint1.longitude(), waypoint1.latitude());
        Coordinate[] coordinates = new Coordinate[] {coordinate, coordinate1};
        LineString line = new GeometryFactory().createLineString(coordinates);
        if (!this.map.getPlayArea().covers(line))
            dist = (double) Long.MAX_VALUE / 2;
        return dist;
    }
}
