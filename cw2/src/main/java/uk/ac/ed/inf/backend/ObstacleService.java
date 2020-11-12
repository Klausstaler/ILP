package uk.ac.ed.inf.backend;

import com.mapbox.geojson.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service used to pull all obstacles from the webserver.
 */
public class ObstacleService extends BackendService {

    private List<LinearRing> obstacles;

    public ObstacleService(String url, String port) throws IOException {
        super(url, port);
        this.setupNewUrl(this.url.toString() + "buildings/no-fly-zones.geojson");
        this.obstacles = this.addObstacles();
    }

    public List<LinearRing> getObstacles() {
        return this.obstacles;
    }

    /**
     * Retrieves the obstacles from the webserver.
     * @return A list of LinearRings, representing the obstacles retrieved.
     * @throws IOException
     */
    private List<LinearRing> addObstacles() throws IOException {
        List<LinearRing> obstacles = new ArrayList<>();

        FeatureCollection features = FeatureCollection.fromJson(this.readResponse());
        for (Feature feature : features.features()) {
            LinearRing obstacle = this.toLinearRing(feature);
            obstacles.add(obstacle);
        }
        return obstacles;
    }

    /**
     * Turns a geoJSON feature into a JTS LinearRing.
     * @param feature geoJSON Feature.
     * @return A linearRing.
     */
    private LinearRing toLinearRing(Feature feature) {
        List<Coordinate> coordinates = new ArrayList<>();

        LineString points = ((Polygon) feature.geometry()).outer();
        for (Point point : points.coordinates()) {
            Coordinate coordinate = new Coordinate(point.longitude(), point.latitude());
            coordinates.add(coordinate);
        }
        return new GeometryFactory().createLinearRing(coordinates.toArray(new Coordinate[0]));
    }
}
