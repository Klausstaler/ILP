package uk.ac.ed.inf;


import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

import java.util.Arrays;
import java.util.Random;

public class GraphOptimizer {

    private static final int SEED = 5678;
    private String startLocation = "0";
    private double[][] distanceMatrix;
    private VehicleRoutingAlgorithm routing;

    public GraphOptimizer(double[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
        this.setupRouting();
    }

    private void setupRouting() {

        var vehicle = this.constructVehicle();
        var costMatrix = this.constructCostMatrix();
        this.routing = this.constructRouter(costMatrix, vehicle);
    }

    private VehicleImpl constructVehicle() {
        VehicleType type =
                VehicleTypeImpl.Builder.newInstance("type").build();
        VehicleImpl vehicle = VehicleImpl.Builder.newInstance("vehicle")
                .setStartLocation(Location.newInstance(this.startLocation))
                .setEndLocation(Location.newInstance(this.startLocation)).setType(type).build();
        return vehicle;
    }

    private VehicleRoutingAlgorithm constructRouter(VehicleRoutingTransportCostsMatrix costMatrix
            , VehicleImpl vehicle) {

        var routingBuilder = VehicleRoutingProblem.Builder.newInstance().setRoutingCost(costMatrix)
                .addVehicle(vehicle);

        for (int i = 0; i < this.distanceMatrix.length; i++) {
            String id = String.valueOf(i);
            var job = Service.Builder.newInstance(id).setLocation(Location.newInstance(id)).build();
            routingBuilder = routingBuilder.addJob(job);
        }

        var algoBuilder = Jsprit.Builder.newInstance(routingBuilder.build());
        return algoBuilder.setRandom(new Random(SEED)).buildAlgorithm();
    }

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

    public int[] optimize() {

        System.out.println("Starting graph optimization....");

        var solutions = this.routing.searchSolutions();

        int[] sol = new int[this.distanceMatrix.length + 1];
        int idx = Integer.parseInt(this.startLocation);
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
        System.out.println("Finished optimizing!");
        return sol;
    }

}