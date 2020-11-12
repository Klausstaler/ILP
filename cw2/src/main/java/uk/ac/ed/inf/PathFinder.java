package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds the shortest path between any to vertices in a graph.
 */
public class PathFinder {

    private final Graph graph; // the graph used
    private List<List<List<Coordinate>>> paths = new ArrayList<>(); // row index i and column
    // index j holds the path to get from node i to node j

    public PathFinder(Graph graph) {
        this.graph = graph;

        // initalize paths lists
        for (int i = 0; i < this.graph.getSize(); i++) {
            List<List<Coordinate>> paths = new ArrayList<>();
            for (int j = 0; j < this.graph.getSize(); j++) {
                List<Coordinate> row = new ArrayList<>();
                var endCoordinate = this.graph.getCoordinate(j);
                row.add(endCoordinate);
                paths.add(row);
            }
            this.paths.add(paths);
        }

        this.calculateShortestPaths();
    }

    /**
     * Gets the shortest path between two vertices
     * @param fromIdx the vertex index from which to start.
     * @param toIdx the index of the vertex where we want to go.
     * @return A List of Coordinates, the path, to get from one vertex to the other.
     */
    public List<Coordinate> getShortestPath(int fromIdx, int toIdx) {
        return this.paths.get(fromIdx).get(toIdx);
    }


    /**
     * Calculates the shortest path between any two vertices in the graph. It uses the
     * Floyd-Warshall algorithm for that. More info under
     * https://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm
     */
    private void calculateShortestPaths() {
        for (int k = 0; k < this.graph.getSize(); k++) {
            for (int i = 0; i < this.graph.getSize(); i++) {
                for (int j = 0; j < this.graph.getSize(); j++) {
                    var currDist = this.graph.getDistance(i, j);
                    var newDist = this.graph.getDistance(i, k);
                    if (newDist < Double.MAX_VALUE) newDist += this.graph.getDistance(k, j);
                    //beware of overflow
                    if (newDist < currDist) { // shorter path found? Update distance and path!
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