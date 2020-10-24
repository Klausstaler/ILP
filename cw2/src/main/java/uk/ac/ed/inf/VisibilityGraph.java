package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisibilityGraph {

    private static final double EPSILON = 0.001;
    private Geometry boundary;
    private List<Coordinate> allCoordinates = new ArrayList<>();
    private List<List<Double>> graph;

    public VisibilityGraph(Geometry boundary) {
        this.boundary = boundary;
        this.graph = this.constructVisibilityGraph();
    }

    private List<List<Double>> constructVisibilityGraph() {
        List<Geometry> boundaries = new ArrayList<>();
        for (int i = 0; i < boundary.getNumGeometries(); i++)
            boundaries.add(boundary.getGeometryN(i));

        for (Geometry obstacle : boundaries) {
            allCoordinates.addAll(Arrays.asList(obstacle.getCoordinates()));
        } // TODO: make extracting all coords a method

        List<List<Double>> visibilityGraph = this.initGraph(allCoordinates);
        this.connectEdges(visibilityGraph, boundaries);

        for (int i = 0; i < allCoordinates.size(); i++) {
            for (int j = 0; j < allCoordinates.size(); j++) {
                if (i == j)
                    continue;
                LineString edge = this.createEdge(allCoordinates.get(i), allCoordinates.get(j));
                if (boundary.covers(edge)) {
                    visibilityGraph.get(i).set(j, edge.getLength());
                }
            }
        }

        return visibilityGraph;
    }

    private List<List<Double>> initGraph(List<Coordinate> coordinates) {
        List<List<Double>> graph = new ArrayList<>();
        for (int i = 0; i < coordinates.size(); i++) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < coordinates.size(); j++) {
                if (i == j)
                    row.add(0.0);
                else
                    row.add(Double.MAX_VALUE);
            }
            graph.add(row);
        }
        return graph;
    }

    private void connectEdges(List<List<Double>> visibilityGraph, List<Geometry> boundaries) {
        int offset = 0;
        for (Geometry obstacle : boundaries) {
            Coordinate[] coordinates = obstacle.getCoordinates();
            for (int idx = 0; idx < coordinates.length - 1; idx++) {
                int pos = idx + offset;
                double dist = coordinates[idx].distance(coordinates[idx + 1]);
                visibilityGraph.get(pos).set(pos + 1, dist);
                visibilityGraph.get(pos + 1).set(pos, dist);
            }
            offset += coordinates.length;
        }
    }

    private LineString createEdge(Coordinate from, Coordinate to) {
        from = from.copy();
        to = to.copy();
        GeometryFactory factory = new GeometryFactory();
        double diff_x = Math.abs(from.x - to.x);
        double diff_y = Math.abs(from.y - to.y);
        from.x += from.x < to.x ? diff_x * EPSILON : -diff_x * EPSILON;
        to.x += from.x > to.x ? diff_x * EPSILON : -diff_x * EPSILON;
        from.y += from.y < to.y ? diff_y * EPSILON : -diff_y * EPSILON;
        to.y += to.y < from.y ? diff_y * EPSILON : -diff_y * EPSILON;
        Coordinate[] edgeCoords = new Coordinate[]{from, to};
        return factory.createLineString(edgeCoords);
    }

    public double[][] getGraph() {
        double[][] graph = new double[this.graph.size()][this.graph.size()];
        for (int i = 0; i < this.graph.size(); i++) {
            graph[i] = this.graph.get(i).stream().mapToDouble(d -> d).toArray();
        }
        return graph;
    }

    public List<Coordinate> getAllCoordinates() {
        return allCoordinates;
    }

    public void addCoordinate(Coordinate toCoordinate) {
        List<Double> newRow = new ArrayList<>();
        for (int i = 0; i < allCoordinates.size(); i++) {
            Coordinate from = allCoordinates.get(i);
            Coordinate[] edgeCoords = new Coordinate[]{from, toCoordinate};
            LineString edge = new GeometryFactory().createLineString(edgeCoords);
            double dist = boundary.covers(edge) ? edge.getLength() : Double.MAX_VALUE;
            graph.get(i).add(dist);
            newRow.add(dist);
        }
        newRow.add(0.0);
        allCoordinates.add(toCoordinate);
        graph.add(newRow);
    }

    public void removeLast() {
        allCoordinates.remove(allCoordinates.size() - 1);
        graph.remove(graph.size() - 1);
        for (List<Double> row : graph) {
            row.remove(row.size() - 1);
        }
    }
}
