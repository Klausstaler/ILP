package uk.ac.ed.inf;

import org.locationtech.jts.geom.Point;

import java.util.*;

public class PathFinder {

    private double[] dists;
    private double[][] distMatrix;
    private Set<Integer> settled = new HashSet<>();
    private PriorityQueue<Node> nodePriorityQueue;
    private HashMap<Integer, List<Integer>> paths = new HashMap<>();

    public PathFinder(double[][] distMatrix) {

        this.nodePriorityQueue = new PriorityQueue<>(distMatrix.length, new Node());
        this.distMatrix = distMatrix;
        this.dists = new double[distMatrix.length];
        for (int i = 0; i < distMatrix.length; i++) {
            dists[i] = Integer.MAX_VALUE;
            this.paths.put(i, new ArrayList<>());
        }
    }

    public static void main(String[] arg) {
        int V = 5;
        int source = 0;

        double[][] distMatrix = new double[V][V];
        for (double[] row : distMatrix) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        distMatrix[0][1] = 9.0;
        distMatrix[0][2] = 6.0;
        distMatrix[0][3] = 5.0;
        distMatrix[0][4] = 3.0;
        distMatrix[2][1] = 2.0;
        distMatrix[2][3] = 4.0;

        // Calculate the single source shortest path
        PathFinder dpq = new PathFinder(distMatrix);
        dpq.shortestPath(0, 3);
    }

    public int[] shortestPath(int src, int target) {
        this.nodePriorityQueue.add(new Node(src, 0));
        this.dists[src] = 0;

        while (!settled.contains(target)) {
            int currNode = this.nodePriorityQueue.remove().node;
            settled.add(currNode);
            this.paths.get(currNode).add(currNode);

            this.expandNeighbors(currNode);
        }
        return this.paths.get(target).stream().mapToInt(i -> i).toArray();
    }

    private void expandNeighbors(int currNode) {

        for (int newNode = 0; newNode < this.distMatrix.length; newNode++) {

            if (!settled.contains(newNode)) {
                double edgeDist = this.distMatrix[currNode][newNode];
                double newDist = this.dists[currNode] + edgeDist;
                if (newDist < this.dists[newNode]) {
                    this.dists[newNode] = newDist;
                    List<Integer> newPath = new ArrayList<>(this.paths.get(currNode));
                    this.paths.put(newNode, newPath);
                }
                nodePriorityQueue.add(new Node(newNode, this.dists[newNode]));
            }
        }
    }
}

// Class to represent a node in the graph
class Node implements Comparator<Node> {
    public int node;
    public double cost;

    public Node() {
    }

    public Node(int node, double cost) {
        this.node = node;
        this.cost = cost;
    }

    @Override
    public int compare(Node node1, Node node2) {
        if (node1.cost < node2.cost)
            return -1;
        if (node1.cost > node2.cost)
            return 1;
        return 0;
    }
}