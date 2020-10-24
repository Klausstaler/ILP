package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisibilityGraph {
    private Map map;
    private List<Coordinate> allCoordinates = new ArrayList<>();
    private List<List<Double>> graph;

    public VisibilityGraph(Map map) {
        this.map = map;
        this.constructVisibilityGraph();
    }

    private void constructVisibilityGraph() {
        this.allCoordinates.addAll(Arrays.asList(this.map.getCoordinates()));

        this.initGraph();
        this.connectEdges();

        for (int i = 0; i < allCoordinates.size(); i++) {
            for (int j = 0; j < allCoordinates.size(); j++) {
                if (i == j)
                    continue;
                Coordinate from = allCoordinates.get(i);
                Coordinate to = allCoordinates.get(j);
                if (this.map.verifyMove(from, to)) {
                    this.graph.get(i).set(j, from.distance(to));
                }
            }
        }
    }

    private void initGraph() {
        List<List<Double>> graph = new ArrayList<>();
        for (int i = 0; i < this.allCoordinates.size(); i++) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < this.allCoordinates.size(); j++) {
                if (i == j)
                    row.add(0.0);
                else
                    row.add(Double.MAX_VALUE);
            }
            graph.add(row);
        }
        this.graph = graph;
    }

    private void connectEdges() {
        int offset = 0;
        Coordinate[] coordinates = this.map.getCoordinates();
        for (int idx = 0; idx < coordinates.length - 1; idx++) {
            int pos = idx + offset;
            double dist = coordinates[idx].distance(coordinates[idx + 1]);
            this.graph.get(pos).set(pos + 1, dist);
            this.graph.get(pos + 1).set(pos, dist);
        }
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

    public void addCoordinate(Coordinate to) {
        List<Double> newRow = new ArrayList<>();
        for (int i = 0; i < allCoordinates.size(); i++) {
            Coordinate from = allCoordinates.get(i);
            double dist = from.distance(to);
            dist = map.verifyMove(from, to) ? dist : Double.MAX_VALUE;
            graph.get(i).add(dist);
            newRow.add(dist);
        }
        newRow.add(0.0);
        allCoordinates.add(to);
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
