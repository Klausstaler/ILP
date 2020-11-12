package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisibilityGraph implements Graph {

    private Map map;
    private List<Coordinate> additionalCoordinates = new ArrayList<>();
    private List<List<Double>> distances;

    public VisibilityGraph(Map map) {
        this.map = map;
        this.constructVisibilityGraph();
    }

    public double getDistance(int fromIdx, int toIdx) {
        return this.distances.get(fromIdx).get(toIdx);
    }

    public void setDistance(int fromIdx, int toIdx, double distance) {
        this.distances.get(fromIdx).set(toIdx, distance);
    }

    public Coordinate getCoordinate(int i) {
        var numMapCoords = this.map.getCoordinates().length;
        if (i >= numMapCoords) {
            return this.additionalCoordinates.get(i - numMapCoords);
        }
        return this.map.getCoordinates()[i];
    }

    public int getSize() {
        return this.distances.size();
    }

    public List<Coordinate> getAllCoordinates() {
        List<Coordinate> allCoordinates = new ArrayList<>();
        allCoordinates.addAll(Arrays.asList(this.map.getCoordinates()));
        allCoordinates.addAll(additionalCoordinates);
        return allCoordinates;
    }

    public void addCoordinate(Coordinate newCoordinate) {
        List<Coordinate> allCoordinates = this.getAllCoordinates();

        List<Double> distanceRow = new ArrayList<>();

        for (int i = 0; i < allCoordinates.size(); i++) {
            Coordinate from = allCoordinates.get(i);
            double dist = from.distance(newCoordinate);

            dist = map.verifyMove(from, newCoordinate) ? dist : Double.MAX_VALUE;
            distances.get(i).add(dist);
            distanceRow.add(dist);
        }
        distanceRow.add(0.0);
        distances.add(distanceRow);

        additionalCoordinates.add(newCoordinate);
    }

    private void constructVisibilityGraph() {

        this.initMatrices();
        this.connectEdges();

        for (int i = 0; i < this.distances.size(); i++) {
            for (int j = i; j < this.distances.size(); j++) {
                var from = this.map.getCoordinates()[i];
                var to = this.map.getCoordinates()[j];
                double dist = from.distance(to);
                if (this.map.verifyMove(from, to)) {
                    this.distances.get(i).set(j, dist);
                    this.distances.get(j).set(i, dist);
                }
            }
        }
    }

    private void initMatrices() {
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
        var coordinates = this.map.getCoordinates();
        for (int idx = 0; idx < coordinates.length - 1; idx++) {
            int pos = idx + offset;
            double dist = coordinates[idx].distance(coordinates[idx + 1]);
            this.distances.get(pos).set(pos + 1, dist);
            this.distances.get(pos + 1).set(pos, dist);
        }
    }
}
