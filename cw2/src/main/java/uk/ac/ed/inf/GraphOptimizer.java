package uk.ac.ed.inf;

import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;

import java.util.Arrays;

public class GraphOptimizer {
    private final int numDrones = 1;
    private int startLocation = 0;
    private long[][] distanceMatrix;
    private RoutingModel routing;
    private RoutingSearchParameters parameters;

    public GraphOptimizer(long[][] distanceMatrix, int startLocation) {
        this.distanceMatrix = distanceMatrix;
        this.startLocation = startLocation;
        this.setupRouting();
    }

    public GraphOptimizer(double[][] distanceMatrix) {
        long[][] distMatrix = new long[distanceMatrix.length][distanceMatrix[0].length];
        for(int i = 0; i < distanceMatrix.length; i++) {
            for(int j = 0; j < distanceMatrix[0].length; j++) {
                distMatrix[i][j] = (long) (Math.pow(10, 8) * distanceMatrix[i][j]);
            }
        }

        /*
        for(long[] row: distMatrix) {
            System.out.println(Arrays.toString(row));
        }
         */
        this.distanceMatrix = distMatrix;
        this.setupRouting();
    }

    public int[] optimize() {
        System.out.println("Starting graph optimization....");

        Assignment solution = routing.solveWithParameters(this.parameters);

        int[] sol = new int[this.distanceMatrix.length+1];
        sol[0] = this.startLocation;
        int idx = 1;
        int currLocation = (int) solution.value(routing.nextVar(this.startLocation));

        while (!routing.isEnd(currLocation)) {
            sol[idx] = currLocation;
            idx++;
            currLocation = (int) solution.value(routing.nextVar(currLocation));
        }
        sol[idx] = this.startLocation;
        System.out.println("NOW SOLUTION");
        System.out.println(Arrays.toString(sol));
        System.out.println("Finished optimizing!");
        return sol;
    }

    private void setupRouting() {
        RoutingIndexManager manager =
                new RoutingIndexManager(this.distanceMatrix.length, this.numDrones,
                        this.startLocation);
        this.routing = new RoutingModel(manager);

        // Define cost of each arc.
        final int transitCallbackIndex =
                routing.registerTransitCallback((long fromIndex, long toIndex) -> {
                    int fromNode = manager.indexToNode(fromIndex);
                    int toNode = manager.indexToNode(toIndex);
                    return this.distanceMatrix[fromNode][toNode];
                });
        this.routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        // Setting solution heuristic.
        this.parameters =
                main.defaultRoutingSearchParameters()
                        .toBuilder()
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                        .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
                        .setTimeLimit(Duration.newBuilder().setSeconds(2).build())
                        .setLogSearch(false)
                        .build();
    }

}