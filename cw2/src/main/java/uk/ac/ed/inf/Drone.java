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
                System.out.println("FLYING TO " + coord);
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
        boolean isFirst = true;
        while (currentCoordinate.distance(to) > SENSOR_RADIUS || isFirst) {
            int angle = this.calculateAngle(currentCoordinate, to);
            System.out.println(angle);
            double new_x = currentCoordinate.x + Math.cos(Math.toRadians(angle))*MOVE_LENGTH;
            double new_y = currentCoordinate.y + Math.sin(Math.toRadians(angle))*MOVE_LENGTH;
            Coordinate newCoordinate = new Coordinate(new_x, new_y);
            System.out.println("Flying from " + currentCoordinate + "to " + newCoordinate);

            Coordinate[] edgeCoords = new Coordinate[]{currentCoordinate, newCoordinate};
            LineString edge = new GeometryFactory().createLineString(edgeCoords);
            if (this.map.inAllowedArea(edge)) {
                this.numMoves++;
                this.logger.log(newCoordinate, collectSensorReading(newCoordinate));
                currentCoordinate = newCoordinate;
            }
            else {
                System.out.println("OUTSIDE AREA M8");
                this.logger.close();
                throw new Exception("BRUH OUTSIDE ALLOWED AREA");
            }
            isFirst = false;
        }
        return currentCoordinate;
    }

    private int calculateAngle(Coordinate from, Coordinate to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double angle = Math.toDegrees(Math.atan2(dy, dx));

        if(angle < 0){
            angle += 360;
        }
        System.out.println("Unrounded angle is " + angle);
        return (int) Math.round(angle / 10) *10;
    }

}
