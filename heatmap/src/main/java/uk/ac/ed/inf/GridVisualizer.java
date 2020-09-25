package uk.ac.ed.inf;

import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GridVisualizer implements Visualizer {

    private Point rectangleSize;
    private int[][] inputGrid;
    private List<Feature> features;

    public GridVisualizer(int[][] grid) {
        this.inputGrid = grid;
        this.rectangleSize = this.calcRectangleSize();
        this.features = this.calcRectangles();
    }

    /***
     * Based on the inputGrid and the rectangle sizes, calculates the GeoJSON Polygons for
     * visualization.
     * @return List of GeoJSON features. (Polygons)
     */
    private List<Feature> calcRectangles() {
        double currLong = MapBoundaries.NORTHWEST.getLongitude();
        double currLat = MapBoundaries.NORTHWEST.getLatitude();
        double longSize = this.rectangleSize.longitude();
        double latSize = this.rectangleSize.latitude();
        ArrayList<Feature> features = new ArrayList<>();

        for (int[] row : this.inputGrid) {
            for (int pollution : row) {
                JsonObject properties = this.getPropertiesByPollution(pollution);

                ArrayList<Point> points = this.computeRectangleCorners(currLong, currLat);

                Polygon polygon = Polygon.fromLngLats(Collections.singletonList(points));
                features.add(Feature.fromGeometry(polygon, properties));

                currLong += longSize;
            }
            currLong = MapBoundaries.NORTHWEST.getLongitude();
            currLat -= latSize;
        }
        return features;
    }

    /***
     * Computes the rectangle corners.
     * @param initialLong Initial longitude of the upper left corner.
     * @param initalLat Initial latitude of the upper left corner.
     * @return Arraylist of all corners in the following ordering: (NW, NE, SE, SW, NW)
     */
    private ArrayList<Point> computeRectangleCorners(double initialLong, double initalLat) {
        double longSize = this.rectangleSize.longitude();
        double latSize = this.rectangleSize.latitude();
        ArrayList<Point> corners = new ArrayList<>();

        corners.add(Point.fromLngLat(initialLong, initalLat));
        corners.add(Point.fromLngLat(initialLong + longSize, initalLat));
        corners.add(Point.fromLngLat(initialLong + longSize, initalLat - latSize));
        corners.add(Point.fromLngLat(initialLong, initalLat - latSize));
        corners.add(Point.fromLngLat(initialLong, initalLat));

        return corners;
    }

    /**
     * Resolves the needed properties for visualization for a given pollution level
     * @param pollution Pollution level
     * @return GeoJSON Feature property specification.
     */
    private JsonObject getPropertiesByPollution(int pollution) {
        String color = MarkerProperties.fromAirPollution(pollution).getRgbString();
        JsonObject properties = new JsonObject();

        properties.addProperty("fill-opacity", 0.75);
        properties.addProperty("fill", color);
        properties.addProperty("rgb-string", color);
        return properties;
    }

    /**
     * Calculates the size of a single heatmap rectangle inside the confinement area.
     * @return GeoJSON Point with longitude and latitude information, represeting the length of
     * the rectangle sides
     */
    private Point calcRectangleSize() {
        double longitudeSize;
        double latitudeSize;
        MapBoundaries ne = MapBoundaries.NORTHEAST;
        MapBoundaries sw = MapBoundaries.SOUTHWEST;
        longitudeSize = (ne.getLongitude() - sw.getLongitude()) / this.inputGrid[0].length;
        latitudeSize = (ne.getLatitude() - sw.getLatitude()) / this.inputGrid.length;
        return Point.fromLngLat(longitudeSize, latitudeSize);
    }

    public FeatureCollection getFeatures() {
        return FeatureCollection.fromFeatures(this.features);
    }
}
