package uk.ac.ed.inf.backend;

import com.mapbox.geojson.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObstacleService extends BackendService {

    private List<LinearRing> obstacles;

    public ObstacleService(String url, String port) throws IOException {
        super(url, port);
        this.setupNewUrl(this.url.toString() + "buildings/no-fly-zones.geojson");
        this.obstacles = this.addObstacles();
    }

    private List<LinearRing> addObstacles() throws IOException {
        List<LinearRing> obstacles = new ArrayList<>();

        FeatureCollection features = FeatureCollection.fromJson(this.readResponse());
        for (Feature feature : features.features()) {
            LinearRing obstacle = this.toLinearRing(feature);
            obstacles.add(obstacle);
        }
        return obstacles;
    }

    private LinearRing toLinearRing(Feature feature) {
        List<Coordinate> coordinates = new ArrayList<>();

        LineString points = ((Polygon) feature.geometry()).outer();
        for (Point point : points.coordinates()) {
            Coordinate coordinate = new Coordinate(point.longitude(), point.latitude());
            coordinates.add(coordinate);
        }
        return new GeometryFactory().createLinearRing(coordinates.toArray(new Coordinate[0]));
    }

    public List<LinearRing> getObstacles() {
        return this.obstacles;
    }
}
