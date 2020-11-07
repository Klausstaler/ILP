package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisibilityGraph implements Graph {
    private Map map;
    private List<Coordinate> additionalCoordinates = new ArrayList<>();
    private List<List<Double>> distances;
    private List<List<Double>> heuristics;

    public VisibilityGraph(Map map) {
        this.map = map;
        this.constructVisibilityGraph();
    }

    public double getDistance(int fromIdx, int toIdx) {
        return this.distances.get(fromIdx).get(toIdx);
    }

    public double getHeuristic(int fromIdx, int toIdx) {
        return this.heuristics.get(fromIdx).get(toIdx);
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
        List<Double> heuristicRow = new ArrayList<>();

        for (int i = 0; i < allCoordinates.size(); i++) {
            Coordinate from = allCoordinates.get(i);
            double dist = from.distance(newCoordinate);

            heuristics.get(i).add(dist);
            heuristicRow.add(dist);

            dist = map.verifyMove(from, newCoordinate) ? dist : Double.MAX_VALUE;
            distances.get(i).add(dist);
            distanceRow.add(dist);
        }
        distanceRow.add(0.0);
        distances.add(distanceRow);

        heuristicRow.add(0.0);
        heuristics.add(heuristicRow);

        additionalCoordinates.add(newCoordinate);
    }

    public void removeLast() {
        additionalCoordinates.remove(additionalCoordinates.size() - 1);
        this.removeLast(this.distances);
        this.removeLast(this.heuristics);
    }
    private void removeLast(List<List<Double>> distMatrix) {
        distMatrix.remove(distMatrix.size() - 1);
        for (List<Double> row : distMatrix) {
            row.remove(row.size() - 1);
        }
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
                this.heuristics.get(i).set(j, dist);
                this.heuristics.get(j).set(i, dist);
            }
        }
    }

    private void initMatrices() {
        List<List<Double>> distances = new ArrayList<>();
        List<List<Double>> heuristics = new ArrayList<>();
        int num_vertices = this.map.getCoordinates().length;
        for (int i = 0; i < num_vertices; i++) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < num_vertices; j++) row.add(Double.MAX_VALUE);
            distances.add(row);
            heuristics.add(new ArrayList<>(row));
        }
        this.distances = distances;
        this.heuristics = heuristics;
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
