package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Drone {

    private static final int MAX_MOVES = 150;
    private static final double SENSOR_RADIUS = 0.0002;
    private static final double MOVE_LENGTH = 0.0003;

    private Coordinate position;
    private RoutePlanner planner;
    private Map map;
    private List<DroneLogger> loggers;
    private HashSet<Coordinate> visited = new HashSet<>();
    private int numMoves = 0;

    public Drone(Coordinate position, Map map, RoutePlanner planner, DroneLogger... loggers) {
        this.position = position;
        this.loggers = Arrays.asList(loggers);
        this.map = map;
        this.planner = planner;
    }

    public void visitSensors() throws Exception {
        System.out.println("Visiting all sensors...");
        List<Coordinate> route = this.planner.getNextPath(position);
        var currCoord = position;
        var referenceCoord = position; // visited waypoint in the range of the current
        // coordinate
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

    private Coordinate getNextValidCoord(Coordinate from, Coordinate to) throws Exception {
        int angle = Angles.calculateAngle(from, to);
        var newCoordinate = Angles.calculateNewCoordinate(from,
                MOVE_LENGTH, angle);
        int oscillationFac = 0; // factor to alternate between expanding angles on left and
        // right of the initial angle
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

    private void log(Coordinate position, Coordinate targetPos) throws IOException {
        Sensor reading = null;
        if (position.distance(targetPos) < SENSOR_RADIUS && targetPos instanceof Sensor) {
            reading = (Sensor) targetPos;
        }
        for (DroneLogger logger : loggers) {
            logger.log(position, reading);
        }
    }

}
