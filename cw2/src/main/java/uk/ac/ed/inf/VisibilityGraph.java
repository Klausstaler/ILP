package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisibilityGraph {
    private Map map;
    private List<Coordinate> additionalCoordinates = new ArrayList<>();
    private List<List<Double>> distances;

    public VisibilityGraph(Map map) {
        this.map = map;
        this.constructVisibilityGraph();
    }

    private void constructVisibilityGraph() {

        this.initDistances();
        this.connectEdges();

        for (int i = 0; i < this.distances.size(); i++) {
            for (int j = i; j < this.distances.size(); j++) {
                Coordinate from = this.map.getCoordinates()[i];
                Coordinate to = this.map.getCoordinates()[j];
                if (this.map.verifyMove(from, to)) {
                    this.distances.get(i).set(j, from.distance(to));
                    this.distances.get(j).set(i, from.distance(to));
                }
            }
        }
    }

    private void initDistances() {
        List<List<Double>> distances = new ArrayList<>();
        int num_vertices = this.map.getCoordinates().length;
        for (int i = 0; i < num_vertices; i++) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < num_vertices; j++) row.add(Double.MAX_VALUE);
            distances.add(row);
        }
        this.distances = distances;
    }

    private void connectEdges() {
        int offset = 0;
        Coordinate[] coordinates = this.map.getCoordinates();
        for (int idx = 0; idx < coordinates.length - 1; idx++) {
            int pos = idx + offset;
            double dist = coordinates[idx].distance(coordinates[idx + 1]);
            this.distances.get(pos).set(pos + 1, dist);
            this.distances.get(pos + 1).set(pos, dist);
        }
    }

    public double[][] getDistances() {
        double[][] distances = new double[this.distances.size()][this.distances.size()];
        for (int i = 0; i < this.distances.size(); i++) {
            distances[i] = this.distances.get(i).stream().mapToDouble(d -> d).toArray();
        }
        return distances;
    }

    public List<Coordinate> getAllCoordinates() {
        List<Coordinate> allCoordinates = new ArrayList<>();
        allCoordinates.addAll(Arrays.asList(this.map.getCoordinates()));
        allCoordinates.addAll(additionalCoordinates);
        return allCoordinates;
    }

    public void addCoordinate(Coordinate to) {
        List<Coordinate> allCoordinates = this.getAllCoordinates();
        List<Double> newRow = new ArrayList<>();
        for (int i = 0; i < allCoordinates.size(); i++) {
            Coordinate from = allCoordinates.get(i);
            double dist = from.distance(to);
            dist = map.verifyMove(from, to) ? dist : Double.MAX_VALUE;
            distances.get(i).add(dist);
            newRow.add(dist);
        }
        newRow.add(0.0);
        additionalCoordinates.add(to);
        distances.add(newRow);
    }

    public void removeLast() {
        additionalCoordinates.remove(additionalCoordinates.size() - 1);
        distances.remove(distances.size() - 1);
        for (List<Double> row : distances) {
            row.remove(row.size() - 1);
        }
    }
}
