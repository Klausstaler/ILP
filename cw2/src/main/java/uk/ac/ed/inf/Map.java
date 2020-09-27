package uk.ac.ed.inf;

import org.locationtech.jts.geom.*;
import uk.ac.ed.inf.backend.ObstacleService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Map {

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private ObstacleService obstacleService;
    private Polygon playArea;


    public Map(ObstacleService obstacleService) {
        this.obstacleService = obstacleService;

        Coordinate[] boundaries = {new Coordinate(-3.192473, 55.946233),
                new Coordinate(-3.184319, 55.946233),
                new Coordinate(-3.184319, 55.942617),
                new Coordinate(-3.192473, 55.942617),
                new Coordinate(-3.192473, 55.946233)};
        LinearRing shell = this.geometryFactory.createLinearRing(boundaries);
        this.playArea = this.geometryFactory.createPolygon(shell);

        LinearRing[] obstacles = this.obstacleService.getObstacles().toArray(new LinearRing[0]);
        this.addObstacles(obstacles);

        System.out.println(this.playArea.isValid());
        System.out.println("Map init ended");
    }

    public boolean inAllowedArea(com.mapbox.geojson.Point position) {
        Point point = this.geometryFactory.createPoint(new Coordinate(position.longitude(),
                        position.latitude()));

        return this.inAllowedArea(point);
    }

    public boolean inAllowedArea(Point position) {
        return this.playArea.intersects(position);
    }

    public void addObstacles(Coordinate[]... obstacles) {

        List<LinearRing> newObstacles = new ArrayList<>();
        for (Coordinate[] obstacle: obstacles) {
            newObstacles.add(this.geometryFactory.createLinearRing(obstacle));
        }
        this.addObstacles(newObstacles.toArray(new LinearRing[0]));
    }

    public void addObstacles(LinearRing... obstacles) {
        obstacles = this.fitObstacles(obstacles);
        Geometry boundaries = this.playArea.getBoundary();
        LinearRing shell = (LinearRing) boundaries.getGeometryN(0);
        List<LinearRing> holes = new ArrayList<>(Arrays.asList(obstacles));

        for(int i = 1; i < boundaries.getNumGeometries(); i++) {
            holes.add((LinearRing) boundaries.getGeometryN(i));
        }

        this.playArea = this.geometryFactory.createPolygon(shell, holes.toArray(new LinearRing[0]));
    }

    private LinearRing[] fitObstacles(LinearRing... obstacles) {
        List<LinearRing> obstaclesInBounds = new ArrayList<>();

        for (LinearRing obstacle : obstacles) {
            if (this.playArea.covers(obstacle)) {
                obstaclesInBounds.add(obstacle);
            }
            else {
                this.alignBoundaries(obstacle);
            }
        }
        return obstaclesInBounds.toArray(new LinearRing[0]);
    }

    private void alignBoundaries(LinearRing obstacle) {
        LinearRing currentShell = (LinearRing) this.playArea.getBoundary().getGeometryN(0);
        MultiLineString allBounds = (MultiLineString) this.playArea.intersection(obstacle);
        List<Coordinate> coordinates = new ArrayList<>(Arrays.asList(currentShell.getCoordinates()));
        coordinates.remove(coordinates.size()-1);
        for(int i = 0; i < allBounds.getNumGeometries(); i++) {
            LineString bound = (LineString) allBounds.getGeometryN(i);
            coordinates.addAll(Arrays.asList(bound.getCoordinates()));
        }
        System.out.println(coordinates);

        //TODO: FIX THIS WITH TSP

        /*
        List<Coordinate> resultBoundary = new ArrayList<>();
        resultBoundary.add(coordinates.get(0));
        HashSet<Integer> usedIdxs = new HashSet<>();
        usedIdxs.add(0);
        for (int i = 1; i < coordinates.size(); i++) {
            Coordinate currCoord = resultBoundary.get(resultBoundary.size()-1);
            int minIdx = i;
            double minDist = 999999999;
            for(int j = 1; j < coordinates.size(); j++) {
                double currDist = currCoord.distance(coordinates.get(j));
                if (currDist < minDist && !usedIdxs.contains(j)) {
                    minDist = currDist;
                    minIdx = j;
                }
            }
            usedIdxs.add(minIdx);
            resultBoundary.add(coordinates.get(minIdx));
        }
        System.out.println(resultBoundary);
         */
    }

}
