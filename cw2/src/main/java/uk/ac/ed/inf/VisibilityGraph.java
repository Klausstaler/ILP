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

    private static final double EPSILON = 0.001;
    private Geometry boundary;
    private List<Coordinate> allCoordinates = new ArrayList<>();
    private List<List<Double>> graph;
    private List<Feature> features = new ArrayList<>();

    public VisibilityGraph(Geometry boundary) throws IOException {
        this.boundary = boundary;
        this.graph = this.constructVisibilityGraph();
    }

    private List<List<Double>> constructVisibilityGraph() throws IOException {
        List<org.locationtech.jts.geom.Geometry> boundaries = new ArrayList<>();
        for(int i = 0; i < boundary.getNumGeometries(); i++)
            boundaries.add(boundary.getGeometryN(i));

        for(org.locationtech.jts.geom.Geometry obstacle: boundaries) {
            allCoordinates.addAll(Arrays.asList(obstacle.getCoordinates()));
        }

        List<List<Double>> visibilityGraph = new ArrayList<>();
        for(int i = 0; i < allCoordinates.size(); i++) {
            List<Double> row = new ArrayList<>();
            for(int j = 0; j < allCoordinates.size(); j++)
                row.add((double) Long.MAX_VALUE / 2);
            visibilityGraph.add(row);
        }

        int offset = 0;
        for (org.locationtech.jts.geom.Geometry obstacle: boundaries) {
            Coordinate[] coordinates = obstacle.getCoordinates();
            for(int idx = 0; idx < coordinates.length-1; idx++) {
                int pos = idx + offset;
                double dist = coordinates[idx].distance(coordinates[idx+1]);
                visibilityGraph.get(pos).set(pos+1, dist);
                visibilityGraph.get(pos+1).set(pos, dist);
            }
            offset += coordinates.length;
        }

        new File("graphviz.geojson").createNewFile();
        FileWriter writer = new FileWriter("graphviz.geojson");

        for(int i = 0; i < allCoordinates.size(); i++) {
            for(int j = 0; j < allCoordinates.size(); j++) {
                if (i == j)
                    continue;
                Coordinate from = allCoordinates.get(i).copy();
                Coordinate to = allCoordinates.get(j).copy();
                LineString edge = this.createEdge(from ,to);
                if (boundary.covers(edge)) {
                    Point point1  = Point.fromLngLat(from.x, from.y);
                    Point point2 = Point.fromLngLat(to.x, to.y);
                    ArrayList<Point> test = new ArrayList<>();
                    test.add(point1);
                    test.add(point2);
                    com.mapbox.geojson.LineString lineString =
                            com.mapbox.geojson.LineString.fromLngLats(test);
                    features.add(Feature.fromGeometry(lineString));
                    visibilityGraph.get(i).set(j, edge.getLength());
                }
            }
        }

        String res =  FeatureCollection.fromFeatures(features).toJson();

        writer.write(res);
        writer.close();

        return visibilityGraph;
    }

    private LineString createEdge(Coordinate from, Coordinate to) {
        GeometryFactory factory = new GeometryFactory();
        double diff_x = Math.abs(from.x - to.x);
        double diff_y = Math.abs(from.y - to.y);
        from.x += from.x < to.x ? diff_x*EPSILON : -diff_x*EPSILON;
        to.x += from.x > to.x ? diff_x*EPSILON : -diff_x*EPSILON;
        from.y += from.y < to.y ? diff_y*EPSILON : -diff_y*EPSILON;
        to.y += to.y < from.y ? diff_y*EPSILON : -diff_y*EPSILON;
        Coordinate[] edgeCoords = new Coordinate[] {from, to};
        return factory.createLineString(edgeCoords);
    }

    public double[][] getGraph() {
        double[][] graph = new double[this.graph.size()][this.graph.size()];
        for(int i = 0; i < this.graph.size(); i++) {
            graph[i] = this.graph.get(i).stream().mapToDouble(d -> d).toArray();
        }
        return graph;
    }

    public List<Coordinate> getAllCoordinates() {
        return allCoordinates;
    }

    public void addCoordinate(Coordinate toCoordinate) {
        List<Double> newRow = new ArrayList<>();
        for(int i = 0; i < allCoordinates.size(); i++) {
            Coordinate from = allCoordinates.get(i);
            Coordinate[] edgeCoords = new Coordinate[] {from, toCoordinate};
            LineString edge = new GeometryFactory().createLineString(edgeCoords);
            double dist = boundary.covers(edge) ? edge.getLength() : (double) Long.MAX_VALUE / 2;
            graph.get(i).add(dist);
            newRow.add(dist);
        }
        newRow.add(0.0);
        allCoordinates.add(toCoordinate);
        graph.add(newRow);
    }

    public void removeLast() {
        allCoordinates.remove(allCoordinates.size()-1);
        graph.remove(graph.size()-1);
        for(List<Double> row: graph) {
            row.remove(row.size()-1);
        }
    }
}
