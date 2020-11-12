package uk.ac.ed.inf;

import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A map of the navigation area.
 */
public class Map extends Polygon {

    private static GeometryFactory geomFact = new GeometryFactory(); // used to instantiate polygon

    public Map(List<LinearRing> obstacles) {
        super(createShell(), getHoles(obstacles), geomFact);
        for (LinearRing obstacle : obstacles) {
            if (!this.covers(obstacle))
                this.alignShell(obstacle);
        }
    }

    /**
     * Creates the outer shell of the map
     * @return A LinearRing representing the outer shell of the map.
     */
    private static LinearRing createShell() {
        Coordinate[] boundaries = {new Coordinate(-3.192473, 55.946233), //NW
                new Coordinate(-3.184319, 55.946233), // NE
                new Coordinate(-3.184319, 55.942617), // SE
                new Coordinate(-3.192473, 55.942617), // SW
                new Coordinate(-3.192473, 55.946233)}; // NW
        return geomFact.createLinearRing(boundaries);
    }

    /**
     * Returns all obstacles fully the outer shell.
     * @param obstacles a List of obstacles represented as a LinearRing.
     * @return An Array of LinearRings, where each of them is fully contained inside the outer
     * shell of the navigation area.
     */
    private static LinearRing[] getHoles(List<LinearRing> obstacles) {
        var provisionalMap = geomFact.createPolygon(createShell());
        List<LinearRing> holes = new ArrayList<>();
        for (LinearRing obstacle : obstacles) {
            if (provisionalMap.covers(obstacle))
                holes.add(obstacle);
        }
        return holes.toArray(new LinearRing[0]);
    }

    /**
     * Verifies a move from a coordinate to another.
     * @param from Coordinate from which we move.
     * @param to Coordinate to which we move.
     * @return Boolean representing whether it is a valid move.
     */
    public boolean verifyMove(Coordinate from, Coordinate to) {
        Coordinate[] edgeCoords = new Coordinate[]{from, to};
        var edge = new GeometryFactory().createLineString(edgeCoords);
        return this.covers(edge);
    }

    /**
     * Given an obstacle that is not fully covered by the outer shell of the navigation area,
     * we align it in such a way that we include the obstacle inside the outer shell.
     * @param obstacle An obstacle that intersects with the outer shell
     */
    private void alignShell(LinearRing obstacle) {
        var newBounds = (MultiLineString) this.intersection(obstacle);
        List<Coordinate> shellCoordinates = new ArrayList<>(Arrays.asList(this.shell.getCoordinates()));
        shellCoordinates.remove(shellCoordinates.size() - 1); // remove last coordinate temporarily to
        // adjust boundary

        List<LineString> orderedBounds = this.orderLines(newBounds);
        int closestIdx = this.getClosestCoordinate(shellCoordinates,
                orderedBounds.get(0).getCoordinateN(0)); // get closest coordinate index in the
        // current outer shell to the first coordinate in our new boundary coordinates and use
        // that as insertion point
        for (LineString bound : orderedBounds) {
            for (Coordinate coordinate : bound.getCoordinates()) {
                shellCoordinates.add(++closestIdx, coordinate);
            }
        }
        shellCoordinates.add(shellCoordinates.get(0)); // add last coordinate back in
        this.shell = geomFact.createLinearRing(shellCoordinates.toArray(new Coordinate[0]));
    }


    /**
     * Gets the index of the closest coordinate in a List of coordinates to another coordinate.
     * @param coordinates List of coordinates in which to find the closest coordinate index
     * @param coordinate The coordinate to which to find the index of the closest coordinate in
     *                   coordinates
     * @return The index of the closest coordinate
     */
    private int getClosestCoordinate(List<Coordinate> coordinates, Coordinate coordinate) {
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


    /**
     * Reverses lines such that the endpoint of one is the start point of the next line
     * @param lines A MultiLineString of lines
     * @return A List of LineStrings, where the endpoint of the current LineString is the
     * start point of the next.
     */
    private List<LineString> orderLines(MultiLineString lines) {
        final double EPSILON = 0.000001; // epsilon used for distance comparison
        List<LineString> orderedLines = new ArrayList<>();

        for (int i = 0; i < lines.getNumGeometries(); i++) {

            var currLine = (LineString) lines.getGeometryN(i);
            Coordinate[] currCoords = currLine.getCoordinates();

            var prevLine = (orderedLines.size() > 0) ?
                    orderedLines.get(orderedLines.size() - 1) : null;
            Coordinate[] prevCoords = prevLine != null ? prevLine.getCoordinates() : null;
            orderedLines.add(currLine);

            // is the start point of the previous line close to the endpoint of our current line?
            // then put the current line before the previous line!
            if (prevCoords != null && prevCoords[0].distance(currCoords[currCoords.length - 1]) < EPSILON) {
                orderedLines.remove(orderedLines.size() - 2); // remove previous line
                orderedLines.add(prevLine);
            }
        }
        return orderedLines;
    }
}
