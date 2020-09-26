package uk.ac.ed.inf;

import com.mapbox.geojson.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.List;

public class Map {

    private static Map instance = null;

    private Polygon playArea;
    private GeometryFactory geometryFactory = new GeometryFactory();


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

        LinearRing[] holes = this.getObstacles();
        LinearRing shell = this.geometryFactory.createLinearRing(boundaries);

        this.playArea = this.geometryFactory.createPolygon(shell, holes);
    }

    public boolean inAllowedArea(Point position) {
        org.locationtech.jts.geom.Point point =
                new GeometryFactory().createPoint(new Coordinate(position.longitude(),
                        position.latitude()));

        return this.inAllowedArea(point);
    }

    public boolean inAllowedArea(org.locationtech.jts.geom.Point position) {
        return this.playArea.intersects(position);
    }

    private LinearRing[] getObstacles() {
        List<LinearRing> obstacles = new ArrayList<>();
        Coordinate[] coordinates = {
                new Coordinate(-3.1923430413007736, 55.94617949887322),
                new Coordinate(-3.1923524290323257,55.94615359004835),
                new Coordinate(-3.192284032702446, 55.94614795769279),
                new Coordinate(-3.192264586687088, 55.94617912338314),
                new Coordinate(-3.1923430413007736, 55.94617949887322)};
        obstacles.add(this.geometryFactory.createLinearRing(coordinates));
        System.out.println(obstacles);
        return obstacles.toArray(new LinearRing[0]);
    }


}
