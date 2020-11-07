package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class PathFinder {

    private final Graph graph;
    private List<List<List<Coordinate>>> paths = new ArrayList<>();

    public PathFinder(Graph graph) {
        this.graph = graph;
        for (int i = 0; i < this.graph.getSize(); i++) {
            List<List<Coordinate>> paths = new ArrayList<>();
            for (int j = 0; j < this.graph.getSize(); j++) {
                List<Coordinate> row = new ArrayList<>();
                var endCoord = this.graph.getCoordinate(j);
                row.add(endCoord);
                paths.add(row);
            }
            this.paths.add(paths);
        }
        this.calculateShortestPaths();
    }

    public List<Coordinate> getShortestPath(int fromIdx, int toIdx) {
        return this.paths.get(fromIdx).get(toIdx);
    }

    private void calculateShortestPaths() {
        for (int k = 0; k < this.graph.getSize(); k++) {
            for (int i = 0; i < this.graph.getSize(); i++) {
                for (int j = 0; j < this.graph.getSize(); j++) {
                    var currDist = this.graph.getDistance(i, j);
                    var newDist = this.graph.getDistance(i, k) + this.graph.getDistance(k, j);
                    if (newDist < currDist) {
                        var firstPart = this.paths.get(i).get(k);
                        var secondPart = this.paths.get(k).get(j);
                        var newPath = new ArrayList<>(firstPart);
                        newPath.addAll(secondPart);
                        this.paths.get(i).set(j, newPath);
                        this.graph.setDistance(i, j, newDist);
                    }
                }
            }
        }
    }
}