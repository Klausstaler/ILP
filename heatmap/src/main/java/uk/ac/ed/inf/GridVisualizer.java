package uk.ac.ed.inf;

import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GridVisualizer {

    private Point rectangleSize;
    private int[][] inputGrid;
    private List<Feature> features;

    public GridVisualizer(int[][] grid) {
        this.inputGrid = grid;
        this.rectangleSize = this.calcRectangleSize();
        this.features = this.calcRectangles();
        System.out.println(FeatureCollection.fromFeatures(this.features));
    }

    private List<Feature> calcRectangles() {
        double currLong= MapBoundaries.NORTHWEST.getLongitude();
        double currLat = MapBoundaries.NORTHWEST.getLatitude();
        double longSize = this.rectangleSize.longitude();
        double latSize = this.rectangleSize.latitude();
        ArrayList<Feature> res = new ArrayList<>();
        for (int[] row: this.inputGrid) {
            for(int pollution: row) {
                String color = MarkerProperties.fromAirPollution(pollution).getRgbString();
                JsonObject properties = new JsonObject();
                properties.addProperty("fill-opacity", 0.75);
                properties.addProperty("fill", color);
                properties.addProperty("rgb-string", color);

                ArrayList<Point> points = new ArrayList<>();
                points.add(Point.fromLngLat(currLong, currLat));
                points.add(Point.fromLngLat(currLong+longSize, currLat));
                points.add(Point.fromLngLat(currLong, currLat-latSize));
                points.add(Point.fromLngLat(currLong+longSize, currLat-latSize));
                currLong += longSize;

                Polygon polygon = Polygon.fromLngLats(Collections.singletonList(points));
                res.add(Feature.fromGeometry(polygon, properties));
            }
            currLong = MapBoundaries.NORTHWEST.getLongitude();
            currLat = currLat - latSize;
        }
        return res;
    }

    private Point calcRectangleSize() {
        double longitudeSize;
        double latitudeSize;
        MapBoundaries ne = MapBoundaries.NORTHEAST;
        MapBoundaries sw = MapBoundaries.SOUTHWEST;
        longitudeSize = (ne.getLongitude() - sw.getLongitude()) / this.inputGrid[0].length;
        latitudeSize = (ne.getLatitude() - sw.getLatitude()) / this.inputGrid.length;
        return Point.fromLngLat(longitudeSize, latitudeSize);
    }
}
