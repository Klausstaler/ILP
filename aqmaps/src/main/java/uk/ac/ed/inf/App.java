package uk.ac.ed.inf;


import org.locationtech.jts.geom.Coordinate;
import uk.ac.ed.inf.backend.ObstacleService;
import uk.ac.ed.inf.backend.SensorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App {
    private static final String URL = "http://localhost"; // base url
    private static Random random; // random seed used in program

    public static void main(String[] args) throws Exception {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "Error"); //
        // disables logging for graphhopper API

        // parse arguments
        String day = args[0];
        String month = args[1];
        String year = args[2];
        double latitude = Double.parseDouble(args[3]);
        double longitude = Double.parseDouble(args[4]);
        random = new Random(Integer.parseInt(args[5]));
        String port = args[6];
        String date = String.format("%s-%s-%s", day, month, year);


        SensorService sensorService = new SensorService(URL, port, day, month, year);

        Coordinate initialPos = new Coordinate(longitude, latitude);

        // setup loggers
        var readingLogger = new ReadingLogger(initialPos, date, sensorService.getSensors());
        var flightPathLogger = new FlightPathLogger(initialPos, date);

        List<Coordinate> waypoints = new ArrayList<>();
        waypoints.add(initialPos);
        waypoints.addAll(sensorService.getSensors());


        var obstacleService = new ObstacleService(URL, port);
        var map = new Map(obstacleService.getObstacles());
        var visibilityGraph = new VisibilityGraph(map);

        var routePlanner = new RoutePlanner(visibilityGraph, waypoints);
        var drone = new Drone(initialPos, map, routePlanner, flightPathLogger, readingLogger);

        drone.visitSensors();
    }

    public static Random getRandom() {
        return random;
    }
}
