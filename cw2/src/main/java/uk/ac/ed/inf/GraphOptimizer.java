package uk.ac.ed.inf;

import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;

import java.util.logging.Logger;

public class GraphOptimizer {
    private final int numDrones = 1;
    private int startLocation = 0;
    private long[][] distanceMatrix;
    private RoutingIndexManager manager;
    private RoutingModel routing;
    private RoutingSearchParameters parameters;

    public GraphOptimizer(long[][] distanceMatrix, int startLocation) {
        this.distanceMatrix = distanceMatrix;
        this.startLocation = startLocation;
        this.manager =
                new RoutingIndexManager(this.distanceMatrix.length, this.numDrones,
                        this.startLocation);
        this.routing = new RoutingModel(manager);

        final int transitCallbackIndex =
                routing.registerTransitCallback((long fromIndex, long toIndex) -> {
                    int fromNode = manager.indexToNode(fromIndex);
                    int toNode = manager.indexToNode(toIndex);
                    return this.distanceMatrix[fromNode][toNode];
                });

        // Define cost of each arc.
        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        // Setting first solution heuristic.
        this.parameters =
                main.defaultRoutingSearchParameters()
                        .toBuilder()
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                        .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
                        .setTimeLimit(Duration.newBuilder().setSeconds(10).build())
                        .setLogSearch(true)
                        .build();
    }

    public GraphOptimizer(long[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public void optimize() {

        Assignment solution = routing.solveWithParameters(this.parameters);
        // Print solution on console.
        printSolution(routing, manager, solution);
    }

    /// @brief Print the solution.
    static void printSolution(
            RoutingModel routing, RoutingIndexManager manager, Assignment solution) {

        Logger logger = Logger.getLogger(TspCities.class.getName());
        // Solution cost.
        logger.info("Objective: " + solution.objectiveValue() + "miles");
        // Inspect solution.
        logger.info("Route:");
        long routeDistance = 0;
        String route = "";
        long index = routing.start(0);
        while (!routing.isEnd(index)) {
            route += manager.indexToNode(index) + " -> ";
            long previousIndex = index;
            index = solution.value(routing.nextVar(index));
            routeDistance += routing.getArcCostForVehicle(previousIndex, index, 0);
        }
        route += manager.indexToNode(routing.end(0));
        logger.info(route);
        logger.info("Route distance: " + routeDistance + "miles");
    }


}