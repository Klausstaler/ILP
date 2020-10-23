package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

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
    private Geometry map;
    private HashSet<Sensor> sensorsToRead = new HashSet<>();
    private List<DroneLogger> loggers;
    private int numMoves = 0;

    public Drone(Coordinate position, Geometry map, List<Sensor> sensors, DroneLogger... loggers) throws Exception {
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
        Coordinate referenceCoord = position;
        while (route.get(route.size()-1) != position) {
            for(Coordinate coord : route) {
                Coordinate newCoord = this.navigate(currCoord, coord);
                currCoord = newCoord;
                referenceCoord = coord;
            }
            route = this.routePlanner.getNextRoute(referenceCoord);
        }
        this.navigate(currCoord, position);
        if (numMoves > MAX_MOVES)
            throw new Exception("too many moves :(");
        this.loggers.forEach((DroneLogger::close));
    }

    private Sensor collectSensorReading(Coordinate coordinate) {
        Sensor read_sensor = null;
        double minDistance = Double.MAX_VALUE;
        for(Sensor sensor : sensorsToRead) {
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

    private Coordinate navigate(Coordinate from, Coordinate to) throws Exception {
        Coordinate currentCoordinate = from;
        boolean isFirstMove = true;
        boolean subtractOfAngle = false;


        System.out.println("NAVIGATING FROM " + from + " TO " + to);
        while (currentCoordinate.distance(to) > SENSOR_RADIUS || isFirstMove) {
            int counter = 0;
            int angle = this.calculateAngle(currentCoordinate, to);
            System.out.println("INITIAL ANGLE " + angle);
            Coordinate newCoordinate = this.getNewCoordinate(currentCoordinate, angle);
            while (!this.verifyMove(currentCoordinate, newCoordinate)) {
                if (subtractOfAngle) {
                    int newAngle = (angle-10*counter);
                    angle = newAngle < 0 ? newAngle + 360 : newAngle;
                }
                else {
                    angle = (angle + 10 * counter) % 360;
                }
                System.out.println("COMPUTED ANGLE " + angle);
                subtractOfAngle = !subtractOfAngle;
                newCoordinate = this.getNewCoordinate(currentCoordinate, angle);
                if (counter++ > 35) {
                    this.loggers.forEach((DroneLogger::close));
                    throw new Exception("BRUH OUTSIDE ALLOWED AREA");
                }
            }
            this.numMoves++;
            System.out.println("ANGLE " + angle);
            System.out.println("FROM " + currentCoordinate + "TO " + newCoordinate);
            Sensor reading = collectSensorReading(newCoordinate);
            for(DroneLogger logger: loggers) {
                logger.log(newCoordinate, reading);
            }
            currentCoordinate = newCoordinate;
            isFirstMove = false;
        }
        System.out.println("ENDING MOVE");
        return currentCoordinate;
    }

    private boolean verifyMove(Coordinate currentCoordinate, Coordinate newCoordinate) {
        Coordinate[] edgeCoords = new Coordinate[]{currentCoordinate, newCoordinate};
        LineString edge = new GeometryFactory().createLineString(edgeCoords);
        return this.map.covers(edge);
    }

    private Coordinate getNewCoordinate(Coordinate currentCoordinate, int angle) {
        double new_x = currentCoordinate.x + Math.cos(Math.toRadians(angle))*MOVE_LENGTH;
        double new_y = currentCoordinate.y + Math.sin(Math.toRadians(angle))*MOVE_LENGTH;
        return new Coordinate(new_x, new_y);
    }

    private int calculateAngle(Coordinate from, Coordinate to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double angle = Math.toDegrees(Math.atan2(dy, dx));

        if(angle < 0){
            angle += 360;
        }
        return (int) Math.round(angle / 10) *10;
    }

}
