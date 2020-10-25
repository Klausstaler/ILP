package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Drone {

    private static final int MAX_MOVES = 150;
    private static final double SENSOR_RADIUS = 0.0002;
    private static final double MOVE_LENGTH = 0.0003;

    private Coordinate position;
    private RoutePlanner routePlanner;
    private Map map;
    private HashSet<Sensor> sensorsToRead = new HashSet<>();
    private List<DroneLogger> loggers;
    private HashSet<Coordinate> visited = new HashSet<>();
    private int numMoves = 0;

    public Drone(Coordinate position, Map map, List<Sensor> sensors, DroneLogger... loggers) throws Exception {
        this.position = position;
        this.loggers = Arrays.asList(loggers);
        this.sensorsToRead.addAll(sensors);
        this.map = map;
        List<Coordinate> waypoints = new ArrayList<>();
        waypoints.add(position);
        waypoints.addAll(sensorsToRead);
        this.routePlanner = new RoutePlanner(map, waypoints);
    }

    public void visitSensors() throws Exception {
        List<Coordinate> route = this.routePlanner.getNextRoute(position);
        Coordinate currCoord = position;
        Coordinate referenceCoord = position; // visited waypoint in the range of the current
        // coordinate
        boolean firstIteration = true;
        while (referenceCoord != position || firstIteration) {
            for (Coordinate coord : route) {
                Coordinate newCoord = this.navigate(currCoord, coord);
                currCoord = newCoord;
                referenceCoord = coord;
            }
            route = this.routePlanner.getNextRoute(referenceCoord);
            firstIteration = false;
        }
        this.loggers.forEach((DroneLogger::close));
        System.out.println("Visited all sensors!");
    }

    private Coordinate navigate(Coordinate from, Coordinate to) throws Exception {
        Coordinate currentCoordinate = from;
        boolean isFirstMove = true;
        System.out.println("NAVIGATING FROM " + from + " TO " + to);
        while (currentCoordinate.distance(to) >= SENSOR_RADIUS || isFirstMove) {
            if (numMoves == MAX_MOVES) {
                this.loggers.forEach((DroneLogger::close));
                throw new Exception("too many moves :(");
            }

            int angle = Angles.calculateAngle(currentCoordinate, to);
            Coordinate newCoordinate = Angles.calculateNewPos(currentCoordinate,
                    MOVE_LENGTH, angle);
            int oscillationFac = 0; // factor to alternate between expanding angles on left and
            // right of the inital angle
            while (!this.map.verifyMove(currentCoordinate, newCoordinate)) {
                angle = Angles.adjustAngle(angle, oscillationFac * 10, oscillationFac % 2 == 1);
                Coordinate candidate = Angles.calculateNewPos(currentCoordinate, MOVE_LENGTH,
                        angle);
                newCoordinate = this.visited.contains(candidate) ? newCoordinate : candidate;
                if (oscillationFac++ > 35) {
                    this.loggers.forEach((DroneLogger::close));
                    throw new Exception("All angles tried, none worked! :(");
                }
            }
            this.log(newCoordinate);
            this.visited.add(newCoordinate);
            currentCoordinate = newCoordinate;
            isFirstMove = false;
            this.numMoves++;
        }
        return currentCoordinate;
    }

    private void log(Coordinate coordinate) {
        Sensor reading = this.collectSensorReading(coordinate);
        for (DroneLogger logger : loggers) {
            logger.log(coordinate, reading);
        }
    }

    private Sensor collectSensorReading(Coordinate coordinate) {
        Sensor read_sensor = null;
        double minDistance = Double.MAX_VALUE;
        for (Sensor sensor : sensorsToRead) {
            double dist = sensor.distance(coordinate);
            if (dist < minDistance) {
                minDistance = dist;
                read_sensor = sensor;
            }
        }
        if (minDistance < SENSOR_RADIUS) {
            sensorsToRead.remove(read_sensor);
            return read_sensor;
        }
        return null;
    }

}
