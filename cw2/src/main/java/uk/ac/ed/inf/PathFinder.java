package uk.ac.ed.inf;


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
        this.initialize();
    }

    public Pair<int[], Double> shortestPath(int fromIdx, int toIdx) {
        this.initialize();
        this.nodePriorityQueue.add(new Node(fromIdx, 0));
        this.dists[fromIdx] = 0;

        while (!settled.contains(toIdx)) {
            int nodeIdx = this.nodePriorityQueue.remove().idx;
            settled.add(nodeIdx);
            this.paths.get(nodeIdx).add(nodeIdx); // add the currentNode as last element
            this.expandNeighbors(nodeIdx);
        }
        int[] path = this.paths.get(toIdx).stream().mapToInt(i -> i).toArray();
        return new Pair<>(path, this.dists[toIdx]);
    }


    private void expandNeighbors(int currNode) {

        for (int nodeIdx = 0; nodeIdx < this.distMatrix.length; nodeIdx++) {
            double edgeDist = this.distMatrix[currNode][nodeIdx];
            if (!settled.contains(nodeIdx)) {
                double newDist = this.dists[currNode] + edgeDist;
                if (newDist < this.dists[nodeIdx]) {
                    this.dists[nodeIdx] = newDist;
                    List<Integer> newPath = new ArrayList<>(this.paths.get(currNode));
                    this.paths.put(nodeIdx, newPath);
                    nodePriorityQueue.add(new Node(nodeIdx, this.dists[nodeIdx]));
                }
            }
        }
    }

    private void initialize() {
        for (int i = 0; i < distMatrix.length; i++) {
            dists[i] = Integer.MAX_VALUE;
            this.paths.put(i, new ArrayList<>());
        }
        this.settled.clear();
        this.nodePriorityQueue.clear();
    }

    public int getNumNodes() {
        return this.distMatrix.length;
    }
}

// Class to represent a node in the graph
class Node implements Comparator<Node> {
    public int idx;
    public double cost;

    public Node() {
    }

    public Node(int idx, double cost) {
        this.idx = idx;
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