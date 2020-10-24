package uk.ac.ed.inf;

import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Map extends Polygon {

    private static GeometryFactory geomFact = new GeometryFactory();

    public Map(List<LinearRing> obstacles) {
        super(createShell(), getHoles(obstacles), geomFact);
        for (LinearRing obstacle : obstacles) {
            if (!this.covers(obstacle))
                this.alignShell(obstacle);
        }
    }

    private static LinearRing createShell() {
        Coordinate[] boundaries = {new Coordinate(-3.192473, 55.946233), //NW
                new Coordinate(-3.184319, 55.946233), // NE
                new Coordinate(-3.184319, 55.942617), // SE
                new Coordinate(-3.192473, 55.942617), // SW
                new Coordinate(-3.192473, 55.946233)}; // NW
        return geomFact.createLinearRing(boundaries);
    }

    private static LinearRing[] getHoles(List<LinearRing> obstacles) {
        Polygon provisionalMap = geomFact.createPolygon(createShell());
        List<LinearRing> holes = new ArrayList<>();
        for (LinearRing obstacle : obstacles) {
            if (provisionalMap.covers(obstacle))
                holes.add(obstacle);
        }
        return holes.toArray(new LinearRing[0]);
    }

    private void alignShell(LinearRing obstacle) {
        MultiLineString newBounds = (MultiLineString) this.intersection(obstacle);
        List<Coordinate> coordinates = new ArrayList<>(Arrays.asList(this.shell.getCoordinates()));
        coordinates.remove(coordinates.size() - 1);

        List<LineString> orderedBounds = this.alignLines(newBounds);
        int closestIdx = this.getClosestPoint(coordinates,
                orderedBounds.get(0).getCoordinateN(0));
        for (LineString bound : orderedBounds) {
            for (Coordinate coordinate : bound.getCoordinates()) {
                coordinates.add(++closestIdx, coordinate);
            }
        }
        coordinates.add(coordinates.get(0));
        this.shell = geomFact.createLinearRing(coordinates.toArray(new Coordinate[0]));
    }

    private int getClosestPoint(List<Coordinate> coordinates, Coordinate coordinate) {
        int closestIdx = 0;
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < coordinates.size(); i++) {
            Coordinate currCoord = coordinates.get(i);
            if (currCoord.distance(coordinate) < minDist) {
                closestIdx = i;
                minDist = currCoord.distance(coordinate);
            }
        }

        return closestIdx;
    }

    private List<LineString> alignLines(MultiLineString lines) {
        final double EPSILON = 0.000001;
        List<LineString> orderedLines = new ArrayList<>();

        for (int i = 0; i < lines.getNumGeometries(); i++) {

            LineString currLine = (LineString) lines.getGeometryN(i);
            Coordinate[] currCoords = currLine.getCoordinates();

            LineString prevLine = (orderedLines.size() > 0) ?
                    orderedLines.get(orderedLines.size() - 1) : null;
            Coordinate[] prevCoords = prevLine != null ? prevLine.getCoordinates() : null;

            if (prevCoords == null || prevCoords[prevCoords.length - 1].distance(currCoords[0]) < EPSILON)
                orderedLines.add(currLine);
            else if (prevCoords[0].distance(currCoords[currCoords.length - 1]) < EPSILON) {
                orderedLines.remove(orderedLines.size() - 1);
                orderedLines.add(currLine);
                orderedLines.add(prevLine);
            }
        }
        return orderedLines;
    }
}
