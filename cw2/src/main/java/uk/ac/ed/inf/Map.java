package uk.ac.ed.inf;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.util.ArrayList;
import java.util.List;

public class Map {

    private static final Point northEastBound = Point.fromLngLat(-3.184319, 55.946233);
    private static final Point southWestBound = Point.fromLngLat(-3.192473, 55.942617);
    private static Map instance = null;
    private List<Polygon> obstacles = new ArrayList<>();

    private Map() {
    }

    public static Map getInstance() {
        if (instance == null) {
            Map.instance = new Map();
        }
        return Map.instance;
    }

    private boolean inMapBoundaries(Point position) {
        double curr_latitude = position.latitude();
        double curr_longitude = position.longitude();
        boolean longitude_check = Map.northEastBound.longitude() > curr_longitude;
        longitude_check =
                longitude_check && (Map.southWestBound.longitude() < curr_longitude);
        boolean latitude_check = Map.northEastBound.latitude() > curr_latitude;
        latitude_check = latitude_check && (Map.southWestBound.latitude() < curr_latitude);
        return latitude_check && longitude_check;
    }

    public boolean inAllowedArea(Point position) {
        boolean inAllowedArea = this.inMapBoundaries(position);

        for (Polygon obstacle : this.obstacles) {
            inAllowedArea = inAllowedArea && this.outsidePolygon(obstacle, position);
        }
        return inAllowedArea;
    }

    private boolean outsidePolygon(Polygon obstacle, Point position) {
        if (this.outsideBoundingRectangle(obstacle, position)) {
            return true;
        }
        List<Point> vertices = obstacle.coordinates().get(0);
        int nVertices = vertices.size();
        int i = 0;
        int j = nVertices;
        boolean inside = false;
        for (i = 0, j = nVertices - 1; i < nVertices; j = i++) {
            Point vert1 = vertices.get(i);
            Point vert2 = vertices.get(j);
        }
    }

    private boolean outsideBoundingRectangle(Polygon obstacle, Point position) {
        List<Point> vertices = obstacle.coordinates().get(0);
        double minLong = vertices.get(0).longitude();
        double maxLong = vertices.get(0).longitude();
        double minLat = vertices.get(0).latitude();
        double maxLat = vertices.get(0).latitude();

        for (int i = 1; i < vertices.size(); i++) {
            Point vertex = vertices.get(1);
            minLong = Math.min(minLong, vertex.longitude();
            maxLong = Math.max(maxLong, vertex.longitude());
            minLat = Math.min(minLat,vertex.latitude());
            maxLat = Math.max(maxLat, vertex.latitude());
        }

        double pLong = position.longitude();
        double pLat = position.latitude();

        boolean outsideLong = pLong < minLong || pLong > maxLong;
        boolean outsideLat = pLat < minLat || pLat > maxLat;

        return outsideLat || outsideLong;
    }


}
