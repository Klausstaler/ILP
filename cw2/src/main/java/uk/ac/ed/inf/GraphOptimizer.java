package uk.ac.ed.inf;

import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;

import java.math.BigDecimal;
import java.math.BigInteger;
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
                BigDecimal num = new BigDecimal(String.valueOf(distanceMatrix[i][j]));
                BigDecimal val = (new BigDecimal("10").pow(16)).multiply(num);
                distMatrix[i][j] = val.longValue();
            }
        }
        this.distanceMatrix = distMatrix;
        this.setupRouting();
    }

    public GraphOptimizer(double[][] distanceMatrix, int startLocation) {
        this(distanceMatrix);
        this.startLocation = startLocation;
    }

    public int[] optimize() {

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
        System.out.println(Arrays.toString(sol));
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
                        .setTimeLimit(Duration.newBuilder().setSeconds(5).build())
                        .setLogSearch(false)
                        .build();
    }

}