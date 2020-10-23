package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import java.util.ArrayList;
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
    private DroneLogger logger;
    private int numMoves = 0;

    public Drone(Coordinate position, DroneLogger logger, Map map, List<Sensor> sensors) throws Exception {
        this.position = position;
        this.logger = logger;
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
        this.logger.close();
    }

    private Sensor collectSensorReading(Coordinate coordinate) {
        Sensor read_sensor = null;
        for(Sensor sensor : sensorsToRead) {
            if (sensor.distance(coordinate) < SENSOR_RADIUS) {
                read_sensor = sensor;
                sensorsToRead.remove(sensor);
                break;
            }
        }
        return read_sensor;
    }

    private Coordinate navigate(Coordinate from, Coordinate to) throws Exception {
        Coordinate currentCoordinate = from;
        boolean isFirstMove = true;
        boolean addToAngle = false;

        while (currentCoordinate.distance(to) > SENSOR_RADIUS || isFirstMove) {
            int angle = this.calculateAngle(currentCoordinate, to);
            Coordinate newCoordinate = this.getNewCoordinate(currentCoordinate, angle);
            int counter = 1;
            while (!this.verifyMove(currentCoordinate, newCoordinate)) {
                angle = addToAngle ? Math.min(350, angle+10*counter) : Math.max(0,
                        angle-10*counter);
                addToAngle = !addToAngle;
                newCoordinate = this.getNewCoordinate(currentCoordinate, angle+10);
                if (counter++ > 35) {
                    System.out.println("OUTSIDE AREA");
                    this.logger.close();
                    throw new Exception("BRUH OUTSIDE ALLOWED AREA");
                }
            }
            this.numMoves++;
            this.logger.log(newCoordinate, collectSensorReading(newCoordinate));
            currentCoordinate = newCoordinate;
            isFirstMove = false;
        }
        return currentCoordinate;
    }

    private boolean verifyMove(Coordinate currentCoordinate, Coordinate newCoordinate) {
        Coordinate[] edgeCoords = new Coordinate[]{currentCoordinate, newCoordinate};
        LineString edge = new GeometryFactory().createLineString(edgeCoords);
        return this.map.inAllowedArea(edge);
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
