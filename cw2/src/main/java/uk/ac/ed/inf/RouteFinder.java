package uk.ac.ed.inf;


import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

import java.util.Arrays;

/**
 * Used to get a short route visiting all nodes in a graph and returning to the start.
 */
public class RouteFinder {

    private String startLocation = "0";
    private double[][] distanceMatrix; // entry in row i and column j represents the distance
    // to get from node i to node j
    private VehicleRoutingAlgorithm routing; // used to get optimal route

    public RouteFinder(double[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
        this.setupRouting();
    }

    /**
     * Finds the shortest route visiting all nodes.
     * @return An array of indices representing the order in which to visit the nodes. A node index
     * at index i means that the node with the given node index should be visited after visiting
     * the node with the node index at index i-1.
     */
    public int[] findShortestRoute() {

        System.out.println("Finding shortest route....");

        var solutions = this.routing.searchSolutions();

        int[] sol = new int[this.distanceMatrix.length + 1]; // last index used to store the
        // start position
        int idx = 0;
        for (var route : Solutions.bestOf(solutions).getRoutes()) {
            var id = route.getStart().getLocation().getId();
            sol[idx++] = Integer.parseInt(id);
            for (var activity : route.getActivities()) {
                id = activity.getLocation().getId();
                // just needed because the library isn't consistent with correct insertion of
                // start location
                if (!id.equals(this.startLocation)) sol[idx++] = Integer.parseInt(id);
            }
        }
        sol[this.distanceMatrix.length] = Integer.parseInt(this.startLocation);
        System.out.println("NOW SOLUTION");
        System.out.println(Arrays.toString(sol));
        System.out.println("Finished finding route!");
        return sol;
    }

    /**
     * Calls all methods used to setup the RouteFinder object.
     */
    private void setupRouting() {

        var vehicle = this.constructVehicle();
        var costMatrix = this.constructCostMatrix();
        this.routing = this.constructRouter(costMatrix, vehicle);
    }

    /**
     * Constructs a Vehicle object. Used in the Graphhopper library to represent an object
     * visiting all nodes.
     * @return A vehicle of type VehicleImpl.
     */
    private VehicleImpl constructVehicle() {
        var type =
                VehicleTypeImpl.Builder.newInstance("type").build();
        var vehicle = VehicleImpl.Builder.newInstance("vehicle")
                .setStartLocation(Location.newInstance(this.startLocation))
                .setEndLocation(Location.newInstance(this.startLocation)).setType(type).build();
        return vehicle;
    }

    /**
     * Constructs the cost matrix for the routing problem.
     * @return A cost matrix.
     */
    private VehicleRoutingTransportCostsMatrix constructCostMatrix() {
        var costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);
        for (int i = 0; i < this.distanceMatrix.length; i++) {
            for (int j = 0; j < i; j++) {
                double dist = this.distanceMatrix[i][j];
                var from = String.valueOf(i);
                var to = String.valueOf(j);
                costMatrixBuilder.addTransportDistance(from, to, dist);
            }
        }
        return costMatrixBuilder.build();
    }

    /**
     * Constructs the routing algorithm used to visit all nodes.
     * @param costMatrix The cost matrix used
     * @param vehicle The vehicle instance used
     * @return The routing algorithm used to visit all nodes
     */
    private VehicleRoutingAlgorithm constructRouter(VehicleRoutingTransportCostsMatrix costMatrix
            , VehicleImpl vehicle) {

        var problembuilder = VehicleRoutingProblem.Builder.newInstance().setRoutingCost(costMatrix)
                .addVehicle(vehicle);

        for (int i = 0; i < this.distanceMatrix.length; i++) {
            String id = String.valueOf(i);
            var job = Service.Builder.newInstance(id).setLocation(Location.newInstance(id)).build();
            problembuilder = problembuilder.addJob(job);
        }

        var routingBuilder = Jsprit.Builder.newInstance(problembuilder.build());
        var routing =
                routingBuilder.setRandom(App.getRandom()).buildAlgorithm(); // use random seed
        routing.setMaxIterations(256);
        return routing;
    }

}