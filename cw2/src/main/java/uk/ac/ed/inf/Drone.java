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
        this.visitSensors();
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
        if (numMoves > MAX_MOVES)
            throw new Exception("too many moves :(");
        this.loggers.forEach((DroneLogger::close));
        System.out.println("Visited all sensors!");
    }

    private Coordinate navigate(Coordinate from, Coordinate to) throws Exception {
        Coordinate currentCoordinate = from;
        boolean isFirstMove = true;
        System.out.println("NAVIGATING FROM " + from + " TO " + to);
        while (currentCoordinate.distance(to) > SENSOR_RADIUS || isFirstMove) {
            int counter = 0; // used to calculate angles for alternative paths if current path is
            // blocked
            int angle = Angles.calculateAngle(currentCoordinate, to);
            Coordinate newCoordinate = this.getNewCoordinate(currentCoordinate, angle);
            while (!this.map.verifyMove(currentCoordinate, newCoordinate)) {
                angle = this.adjustAngle(angle, counter);
                Coordinate candidate = this.getNewCoordinate(currentCoordinate, angle);
                newCoordinate = this.visited.contains(candidate) ? newCoordinate : candidate;
                if (counter++ > 35) {
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

    private int adjustAngle(int angle, int oscillation) {
        if (oscillation % 2 == 1) { // oscillate between expanding left and right
            // half of possible angles
            int newAngle = (angle - 10 * oscillation) % 360;
            angle = newAngle < 0 ? newAngle + 360 : newAngle;
        } else {
            angle = (angle + 10 * oscillation) % 360;
        }
        return angle;
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

    private Coordinate getNewCoordinate(Coordinate currentCoordinate, int angle) {
        double new_x = currentCoordinate.x + Math.cos(Math.toRadians(angle)) * MOVE_LENGTH;
        double new_y = currentCoordinate.y + Math.sin(Math.toRadians(angle)) * MOVE_LENGTH;
        return new Coordinate(new_x, new_y);
    }

}
