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

    public Pair<int[], Double> shortestPath(int src, int target) {
        this.initialize();
        this.nodePriorityQueue.add(new Node(src, 0));
        this.dists[src] = 0;

        while (!settled.contains(target)) {
            int currNode = this.nodePriorityQueue.remove().node;
            settled.add(currNode);
            this.paths.get(currNode).add(currNode);
            this.expandNeighbors(currNode);
        }
        int[] path = this.paths.get(target).stream().mapToInt(i -> i).toArray();
        return new Pair<>(path, this.dists[target]);
    }


    private void expandNeighbors(int currNode) {

        for (int newNode = 0; newNode < this.distMatrix.length; newNode++) {
            double edgeDist = this.distMatrix[currNode][newNode];
            if (!settled.contains(newNode)) {
                double newDist = this.dists[currNode] + edgeDist;
                if (newDist < this.dists[newNode]) {
                    this.dists[newNode] = newDist;
                    List<Integer> newPath = new ArrayList<>(this.paths.get(currNode));
                    this.paths.put(newNode, newPath);
                    nodePriorityQueue.add(new Node(newNode, this.dists[newNode]));
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