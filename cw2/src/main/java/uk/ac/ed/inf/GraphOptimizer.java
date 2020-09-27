package uk.ac.ed.inf;

public class GraphOptimizer {
    private final int numDrones = 1;
    private int startLocation = 0;
    private long[][] distanceMatrix;

    public GraphOptimizer(long[][] distanceMatrix, int startLocation) {
        this.distanceMatrix = distanceMatrix;
        this.startLocation = startLocation;
        Routing manager =
                new RoutingIndexManager(data.distanceMatrix.length, data.vehicleNumber, data.depot);
        RoutingModel routing = new RoutingModel(manager);
    }

    public GraphOptimizer(long[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }


}
