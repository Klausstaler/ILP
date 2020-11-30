package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Used to log the flight path and markers in geoJSON format.
 */
public class ReadingLogger extends DroneLogger {

    private List<Coordinate> flightPath = new ArrayList<>(); // flight path of drone
    private HashMap<String, Feature> markers = new HashMap<>(); // maps what3words address
    // to a marker

    public ReadingLogger(Coordinate initialPos, String date, List<Sensor> sensors) throws IOException {
        super(initialPos, "readings-" + date + ".geojson");
        this.flightPath.add(initialPos);
        for (var sensor : sensors) { // initialize all markers as not visited
            Feature feature = Feature.fromGeometry(Point.fromLngLat(sensor.x, sensor.y));
            feature.addStringProperty("marker-color",
                    MarkerProperties.NOTVISITED.getRgbString());
            markers.put(sensor.getLocation(), feature);
        }
        System.out.println("Reading logger initialized...");
    }

    /**
     * Adds the new position to the flight path while updating the marker properties of the read
     * sensor.
     * @param newPos the new position we need to log
     * @param read_sensor The sensor read during move
     */
    @Override
    public void log(Coordinate newPos, Sensor read_sensor) {
        this.flightPath.add(newPos);
        this.position = newPos;
        if (read_sensor != null) {
            this.updateMarkerProps(read_sensor);
        }
    }

    /**
     * Dumps the markers and flightpath into the output file in geoJSON format, then closes it.
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        List<Feature> allFeatures = new ArrayList<>(this.markers.values());
        List<Point> flightPathGeo = new ArrayList<>();
        for (Coordinate coord : this.flightPath)
            flightPathGeo.add(Point.fromLngLat(coord.x, coord.y));

        var flightPath = LineString.fromLngLats(flightPathGeo);
        allFeatures.add(Feature.fromGeometry(flightPath));

        FeatureCollection features = FeatureCollection.fromFeatures(allFeatures);

        System.out.println("ReadingLogger closing file..");
        this.file.write(features.toJson());
        this.file.close();
    }

    /**
     * Updates the marker properties of the marker located at the sensor's what3words address.
     * @param read_sensor the sensor read.
     */
    private void updateMarkerProps(Sensor read_sensor) {
        var marker = this.markers.get(read_sensor.getLocation());
        var markerProps = (read_sensor.getReading() == null) ?
                MarkerProperties.LOWBATTERY :
                MarkerProperties.fromAirPollution(read_sensor.getReading());

        marker.removeProperty("marker-color");
        marker.addStringProperty("marker-color", markerProps.getRgbString());
        marker.addStringProperty("marker-symbol", markerProps.getMarkerSymbol());
    }
}
