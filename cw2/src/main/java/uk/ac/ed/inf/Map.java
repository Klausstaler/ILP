package uk.ac.ed.inf;

import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Map {

    private static Map instance = null;

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private Polygon playArea;


    public static Map getInstance() {
        if (instance == null) {
            Map.instance = new Map();
        }
        return Map.instance;
    }

    private Map() {
        Coordinate[] boundaries = {new Coordinate(-3.192473, 55.946233),
                new Coordinate(-3.184319, 55.946233),
                new Coordinate(-3.184319, 55.942617),
                new Coordinate(-3.192473, 55.942617),
                new Coordinate(-3.192473, 55.946233)};
        LinearRing shell = this.geometryFactory.createLinearRing(boundaries);
        this.playArea = this.geometryFactory.createPolygon(shell);
        this.getObstacles();
        System.out.println(this.playArea);
    }

    public boolean inAllowedArea(com.mapbox.geojson.Point position) {
        org.locationtech.jts.geom.Point point =
                new GeometryFactory().createPoint(new Coordinate(position.longitude(),
                        position.latitude()));

        return this.inAllowedArea(point);
    }

    public boolean inAllowedArea(Point position) {
        return this.playArea.intersects(position);
    }

    private void getObstacles() {
        List<LinearRing> obstacles = new ArrayList<>();
        Coordinate[] coordinates = {
                new Coordinate(-3.1923430413007736, 55.94617949887322),
                new Coordinate(-3.1923524290323257,55.94615359004835),
                new Coordinate(-3.192284032702446, 55.94614795769279),
                new Coordinate(-3.192264586687088, 55.94617912338314),
                new Coordinate(-3.1923430413007736, 55.94617949887322)};
        obstacles.add(this.geometryFactory.createLinearRing(coordinates));
        obstacles.add(this.geometryFactory.createLinearRing(coordinates));

        this.addObstacles(obstacles.toArray(new LinearRing[0]));
    }

    public void addObstacles(Coordinate[]... obstacles) {

        List<LinearRing> newObstacles = new ArrayList<>();
        for (Coordinate[] obstacle: obstacles) {
            newObstacles.add(this.geometryFactory.createLinearRing(obstacle));
        }
        this.addObstacles(newObstacles.toArray(new LinearRing[0]));
    }

    public void addObstacles(LinearRing... obstacles) {
        Geometry boundaries = this.playArea.getBoundary();
        LinearRing shell = (LinearRing) boundaries.getGeometryN(0);
        List<LinearRing> holes = new ArrayList<>(Arrays.asList(obstacles));
        for(int i = 1; i < boundaries.getNumGeometries(); i++) {
            holes.add((LinearRing) boundaries.getGeometryN(i));
        }
        this.playArea = this.geometryFactory.createPolygon(shell, holes.toArray(new LinearRing[0]));
    }

}
