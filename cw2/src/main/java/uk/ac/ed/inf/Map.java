package uk.ac.ed.inf;

import org.locationtech.jts.geom.*;
import uk.ac.ed.inf.backend.ObstacleService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Map {

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private Polygon playArea;


    public Map(ObstacleService obstacleService) {

        Coordinate[] boundaries = {new Coordinate(-3.192473, 55.946233),
                new Coordinate(-3.184319, 55.946233),
                new Coordinate(-3.184319, 55.942617),
                new Coordinate(-3.192473, 55.942617),
                new Coordinate(-3.192473, 55.946233)};
        LinearRing shell = this.geometryFactory.createLinearRing(boundaries);
        this.playArea = this.geometryFactory.createPolygon(shell);

        LinearRing[] obstacles = obstacleService.getObstacles().toArray(new LinearRing[0]);
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
        System.out.println(Arrays.toString(allBounds.getCoordinates()));
        for(int i = 0; i < allBounds.getNumGeometries(); i++) {
            LineString bound = (LineString) allBounds.getGeometryN(i);
            coordinates.addAll(Arrays.asList(bound.getCoordinates()));
            System.out.println(Arrays.toString(bound.getCoordinates()));
        }

        // TODO: get coordinates out of it, have edges, align edges into rest of coordinates by
        //  connecting to closest vertices
        coordinates.addAll(allBounds.getCoordinates())
        System.out.println(coordinates);

        //TODO: FIX THIS WITH TSP
        double[][] distances = new double[coordinates.size()][coordinates.size()];
        for(int i = 0; i < coordinates.size(); i++) {
            for(int j = 0; j < coordinates.size()-1; j++) {
                double dist = coordinates.get(i).distance(coordinates.get(j));
                distances[i][j] = dist;
                distances[j][i] = dist;
            }
        }
        for(int i = 0; i < distances.length; i++) {
            for(int j  = 0; j < distances.length; j++) {
                double dist = distances[i][j];
                if (dist == 0)
                    System.out.println(i + " " + j);
            }
        }
        GraphOptimizer optimizer = new GraphOptimizer(distances);
        int[] route = optimizer.optimize();
        System.out.println(Arrays.toString(route));

        List<Coordinate> newCoordinates = new ArrayList<>();
        for(int idx: route) {
            newCoordinates.add(coordinates.get(idx));
        }

        System.out.println("BOUNDARY" + newCoordinates);
        LinearRing newShell =
                this.geometryFactory.createLinearRing(newCoordinates.toArray(new Coordinate[0]));
        List<LinearRing> obstacles = this.getObstacles();
        Polygon playArea = this.geometryFactory.createPolygon(newShell,
                obstacles.toArray(new LinearRing[0]));
        System.out.println(playArea.isValid());

    }

    public List<LinearRing> getObstacles() {
        List<LinearRing> obstacles = new ArrayList<>();
        for(int i = 1; i < this.playArea.getBoundary().getNumGeometries(); i++ ) {
            obstacles.add( (LinearRing) this.playArea.getBoundary().getGeometryN(i));
        }
        return obstacles;
    }

}
