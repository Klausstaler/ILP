package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Drone class used to visit all sensors.
 */
public class Drone {

    private static final int MAX_MOVES = 150; // maximum number of moves the drone is allowed to
    // take
    private static final double SENSOR_RADIUS = 0.0002; // maximum distance to sensor to collect
    // reading
    private static final double MOVE_LENGTH = 0.0003; // distance travelled in one move

    private Coordinate position; // the start position of the drone
    private RoutePlanner planner; // planner used for routing between all waypoints
    private Map map; // map to check if we do valid moves
    private List<DroneLogger> loggers; // loggers to create output files
    private HashSet<Coordinate> visited = new HashSet<>(); // used to not go to the same location
    // twice
    private int numMoves = 0; // number of moves made so far

    public Drone(Coordinate position, Map map, RoutePlanner planner, DroneLogger... loggers) {
        this.position = position;
        this.loggers = Arrays.asList(loggers);
        this.map = map;
        this.planner = planner;
    }

    /**
     * Follows the route returned by the routePlanner while visiting all sensors in the route.
     * @throws Exception
     */
    public void visitSensors() throws Exception {
        System.out.println("Visiting all sensors...");
        List<Coordinate> route = this.planner.getNextPath(position);
        var currCoord = position;
        var referenceCoord = position; // used to get the next path we need to follow from the
        // routePlanner
        boolean firstIteration = true;
        while (referenceCoord != position || firstIteration) {
            for (Coordinate coord : route) {
                currCoord = this.navigate(currCoord, coord);
                referenceCoord = coord;
            }
            route = this.planner.getNextPath(referenceCoord);
            firstIteration = false;
        }
        for (var logger : this.loggers) logger.close();
        System.out.println("Finished visiting all sensors!");
    }

    /**
     * Used to move from one coordinate to another. Guarantees that after successful execution we
     * are less than SENSOR_RADIUS away from the to coordinate.
     * @param from The coordinate from which we start.
     * @param to The coordinate we want to get close to.
     * @return The final coordinate which is a distance of less than SENSOR_RADIUS away from the
     * to coordinate.
     * @throws Exception
     */
    private Coordinate navigate(Coordinate from, Coordinate to) throws Exception {
        var currentCoordinate = from;
        boolean isFirstMove = true;
        while (currentCoordinate.distance(to) >= SENSOR_RADIUS || isFirstMove) {
            if (numMoves == MAX_MOVES) {
                for (DroneLogger logger : this.loggers) logger.close();
                throw new Exception("too many moves :(");
            }
            var newCoordinate = this.getNextValidCoord(currentCoordinate, to);
            this.log(newCoordinate, to);
            this.visited.add(newCoordinate);
            currentCoordinate = newCoordinate;
            isFirstMove = false;
            this.numMoves++;
        }
        return currentCoordinate;
    }

    /**
     * Gets the next coordinate inside the navigation area and which was not visited before that
     * should get us closer to the to coordinate.
     * @param from The coordinate from which we start.
     * @param to The coordinate we want to get closer to.
     * @return A new Coordinate, which should be closer to the to coordinate.
     * @throws Exception
     */
    private Coordinate getNextValidCoord(Coordinate from, Coordinate to) throws Exception {
        int angle = Angles.calculateAngle(from, to);
        var newCoordinate = Angles.calculateNewCoordinate(from,
                MOVE_LENGTH, angle);
        int oscillationFac = 0; // factor to alternate between expanding angles on left and
        // right of the initial angle

        // oscillate between expanding angles greater or smaller than the exact angle until we
        // find a coordinate we can move to
        while (!this.map.verifyMove(from, newCoordinate)) {
            angle = Angles.adjustAngle(angle, oscillationFac * 10, oscillationFac % 2 == 1);
            var candidate = Angles.calculateNewCoordinate(from, MOVE_LENGTH,
                    angle);
            newCoordinate = this.visited.contains(candidate) ? newCoordinate : candidate;
            if (oscillationFac++ > 35) {
                for (var logger : this.loggers) logger.close();
                throw new Exception("All angles tried, none worked! :(");
            }
        }
        return newCoordinate;
    }

    /**
     * Logs the required data using the loggers.
     * @param position The position we want to log.
     * @param targetPos the target position where we want to get to. Used to identify whether we
     *                  can read a sensor.
     * @throws IOException
     */
    private void log(Coordinate position, Coordinate targetPos) throws IOException {
        Sensor reading = null;

        // the routePlanner returns us a route visiting all sensors, where the endpoints of the
        // route are the downcasted Coordinates of the Sensor. Hence we can upcast to get the
        // Sensor.
        if (position.distance(targetPos) < SENSOR_RADIUS && targetPos instanceof Sensor) {
            reading = (Sensor) targetPos;
        }
        for (DroneLogger logger : loggers) {
            logger.log(position, reading);
        }
    }

}
