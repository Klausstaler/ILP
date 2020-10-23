package uk.ac.ed.inf;

import org.locationtech.jts.geom.*;

import java.util.*;

public class Map {

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private Polygon playArea;


    public Map(List<LinearRing> obstacles) {

        Coordinate[] boundaries = {new Coordinate(-3.192473, 55.946233), //NW
                new Coordinate(-3.184319, 55.946233), // NE
                new Coordinate(-3.184319, 55.942617), // SE
                new Coordinate(-3.192473, 55.942617), // SW
                new Coordinate(-3.192473, 55.946233)}; // NW
        LinearRing shell = this.geometryFactory.createLinearRing(boundaries);
        this.playArea = this.geometryFactory.createPolygon(shell);


        this.addObstacles(obstacles);
    }

    public boolean inAllowedArea(Geometry position) {
        return this.playArea.covers(position);
    }

    public void addObstacles(List<LinearRing> obstacles) {

        List<LinearRing> holes = this.fitObstacles(obstacles);

        Geometry boundaries = this.playArea.getBoundary();
        LinearRing shell = (LinearRing) boundaries.getGeometryN(0);
        for(int i = 1; i < boundaries.getNumGeometries(); i++) {
            holes.add((LinearRing) boundaries.getGeometryN(i));
        }

        this.playArea = this.geometryFactory.createPolygon(shell, holes.toArray(new LinearRing[0]));
    }

    private List<LinearRing> fitObstacles(List<LinearRing> obstacles) {
        List<LinearRing> obstaclesInBounds = new ArrayList<>();

        for (LinearRing obstacle : obstacles) {
            if (this.playArea.covers(obstacle)) {
                obstaclesInBounds.add(obstacle);
            }
            else {
                this.alignBoundaries(obstacle);
            }
        }
        return obstaclesInBounds;
    }

    private void alignBoundaries(LinearRing obstacle) {
        LinearRing currentShell = (LinearRing) this.playArea.getBoundary().getGeometryN(0);
        MultiLineString newBounds = (MultiLineString) this.playArea.intersection(obstacle);
        List<Coordinate> coordinates = new ArrayList<>(Arrays.asList(currentShell.getCoordinates()));
        coordinates.remove(coordinates.size()-1);

        List<LineString> orderedBounds = this.alignLines(newBounds);

        List<Coordinate> boundCoords = new ArrayList<>();
        for(LineString bound: orderedBounds)
            boundCoords.addAll(Arrays.asList(bound.getCoordinates()));


        int closestIdx = this.getClosestPoint(coordinates, boundCoords.get(0));

        for(Coordinate newCoord : boundCoords) {
            coordinates.add(++closestIdx, newCoord);
        }
        coordinates.add(coordinates.get(0));

        LinearRing shell =
                this.geometryFactory.createLinearRing(coordinates.toArray(new Coordinate[0]));
        this.playArea = this.geometryFactory.createPolygon(shell,
                this.getObstacles().toArray(new LinearRing[0]));
    }

    private int getClosestPoint(List<Coordinate> coordinates, Coordinate coordinate) {
        int closestIdx = 0;
        double minDist = 999999;
        for(int i = 0; i < coordinates.size(); i++) {
            Coordinate currCoord = coordinates.get(i);
            if (currCoord.distance(coordinate) < minDist) {
                closestIdx = i;
                minDist = currCoord.distance(coordinate);
            }
        }

        return closestIdx;
    }

    private List<LineString> alignLines(MultiLineString lines) {
        double EPSILON = 0.000001;
        List<LineString> orderedLines = new ArrayList<>();

        for(int i = 0; i < lines.getNumGeometries(); i++) {

            LineString currLine = (LineString) lines.getGeometryN(i);
            Coordinate[] currCoords = currLine.getCoordinates();

            LineString prevLine = (orderedLines.size() > 0) ?
                    orderedLines.get(orderedLines.size()-1) : null;
            Coordinate[] prevCoords = prevLine != null ? prevLine.getCoordinates() : null;

            if (prevCoords == null || prevCoords[prevCoords.length-1].distance(currCoords[0]) < EPSILON)
                orderedLines.add(currLine);
            else if (prevCoords[0].distance(currCoords[currCoords.length-1]) < EPSILON) {
                orderedLines.remove(orderedLines.size()-1);
                orderedLines.add(currLine);
                orderedLines.add(prevLine);
            }
        }
        return orderedLines;
    }

    public List<Geometry> getObstacles() {
        List<Geometry> obstacles = new ArrayList<>();
        for(int i = 1; i < this.playArea.getBoundary().getNumGeometries(); i++ ) {
            obstacles.add(this.playArea.getBoundary().getGeometryN(i));
        }
        return obstacles;
    }

    public Geometry getPlayArea() {
        return this.playArea;
    }
}
