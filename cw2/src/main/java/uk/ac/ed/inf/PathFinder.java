package uk.ac.ed.inf;


import java.util.*;

public class PathFinder {

    private double[] dists;
    private final Graph graph;
    private Set<Integer> settled = new HashSet<>();
    private PriorityQueue<Node> nodePriorityQueue;
    private HashMap<Integer, List<Integer>> paths = new HashMap<>();

    public PathFinder(Graph graph) {

        this.nodePriorityQueue = new PriorityQueue<>(graph.getSize(), new Node());
        this.graph = graph;
        this.dists = new double[graph.getSize()];
        for (int i = 0; i < this.graph.getSize(); i++) {
            dists[i] = Integer.MAX_VALUE;
            this.paths.put(i, new ArrayList<>());
        }
    }

    public Pair<int[], Double> shortestPath(int fromIdx, int toIdx) {
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

        for (int nodeIdx = 0; nodeIdx < this.graph.getSize(); nodeIdx++) {
            double edgeDist = this.graph.getDistance(currNode, nodeIdx);
            if (!settled.contains(nodeIdx)) {
                double newDist = this.dists[currNode] + edgeDist;
                if (newDist < this.dists[nodeIdx]) {
                    this.dists[nodeIdx] = newDist;
                    List<Integer> newPath = new ArrayList<>(this.paths.get(currNode));
                    this.paths.put(nodeIdx, newPath);
                    double heuristicCost = this.graph.getHeuristic(currNode, nodeIdx);
                    nodePriorityQueue.add(new Node(nodeIdx, this.dists[nodeIdx] + heuristicCost));
                }
            }
        }
    }

    public int getNumNodes() {
        return this.graph.getSize();
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