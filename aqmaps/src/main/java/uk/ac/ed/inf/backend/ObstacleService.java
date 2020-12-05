package uk.ac.ed.inf.backend;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Polygon;
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

    public ObstacleService(String url, String port) throws IOException, InterruptedException {
        super(url, port);
        this.obstacles = this.addObstacles();
    }

    public List<LinearRing> getObstacles() {
        return this.obstacles;
    }

    /**
     * Retrieves the obstacles from the webserver.
     *
     * @return A list of LinearRings, representing the obstacles retrieved.
     * @throws IOException
     */
    private List<LinearRing> addObstacles() throws IOException, InterruptedException {
        List<LinearRing> obstacles = new ArrayList<>();

        var features = FeatureCollection.fromJson(this.getResponse(this.baseUrl + "buildings/no" +
                "-fly-zones.geojson"));
        for (var feature : features.features()) {
            var obstacle = this.toLinearRing(feature);
            obstacles.add(obstacle);
        }
        return obstacles;
    }

    /**
     * Turns a geoJSON feature into a JTS LinearRing.
     *
     * @param feature geoJSON Feature.
     * @return A linearRing.
     */
    private LinearRing toLinearRing(Feature feature) {
        List<Coordinate> coordinates = new ArrayList<>();

        LineString points = ((Polygon) feature.geometry()).outer();
        for (var point : points.coordinates()) {
            var coordinate = new Coordinate(point.longitude(), point.latitude());
            coordinates.add(coordinate);
        }
        return new GeometryFactory().createLinearRing(coordinates.toArray(new Coordinate[0]));
    }
}
