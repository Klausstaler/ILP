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

        }
        return inAllowedArea;
    }
}
