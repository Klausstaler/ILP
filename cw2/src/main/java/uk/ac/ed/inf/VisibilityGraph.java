package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisibilityGraph {

    private double[][] graph;
    private List<Feature> features = new ArrayList<>();

    public VisibilityGraph(Geometry map) throws IOException {
        this.graph = this.constructVisibilityGraph(map);
    }

    private double[][] constructVisibilityGraph(Geometry map) throws IOException {

        List<org.locationtech.jts.geom.Geometry> boundaries = new ArrayList<>();
        for(int i = 0; i < map.getNumGeometries(); i++)
            boundaries.add(map.getGeometryN(i));


        List<Coordinate> allCoordinates = new ArrayList<>();
        for(org.locationtech.jts.geom.Geometry obstacle: boundaries) {
            allCoordinates.addAll(Arrays.asList(obstacle.getCoordinates()));
        }


        double[][] visibilityGraph = new double[allCoordinates.size()][allCoordinates.size()];
        for(double[] row: visibilityGraph)
            Arrays.fill(row, (double) Long.MAX_VALUE / 2);
        int offset = 0;
        for (org.locationtech.jts.geom.Geometry obstacle: boundaries) {
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
                if (map.covers(edge)) {
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
            }
        }

        String res =  FeatureCollection.fromFeatures(features).toJson();
        //System.out.println(res);

        writer.write(res);
        writer.close();

        return visibilityGraph;
    }
}
