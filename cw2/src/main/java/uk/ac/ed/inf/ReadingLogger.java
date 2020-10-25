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

public class ReadingLogger extends DroneLogger {

    private static final double MIN_BATTERY = 10.0;

    private List<Coordinate> flightPath = new ArrayList<>();
    private HashMap<String, Feature> markers = new HashMap<>();

    public ReadingLogger(Coordinate initialPos, String date, List<Sensor> sensors) throws IOException {
        super(initialPos, "readings-" + date + ".geojson");
        this.flightPath.add(initialPos);
        for (Sensor sensor : sensors) {
            Feature feature = Feature.fromGeometry(Point.fromLngLat(sensor.x, sensor.y));
            feature.addStringProperty("marker-color",
                    MarkerProperties.NOTVISITED.getRgbString());
            markers.put(sensor.getLocation(), feature);
        }
        System.out.println("Reading logger initialized...");
    }

    @Override
    public void log(Coordinate newPos, Sensor read_sensor) {
        this.flightPath.add(newPos);
        this.position = newPos;
        if (read_sensor != null) {
            this.updateMarkerProps(read_sensor);
        }
    }

    @Override
    public void close() {
        List<Feature> allFeatures = new ArrayList<>(this.markers.values());
        List<com.mapbox.geojson.Point> flightPathGeo = new ArrayList<>();
        for (Coordinate coord : this.flightPath)
            flightPathGeo.add(com.mapbox.geojson.Point.fromLngLat(coord.x, coord.y));

        LineString flightPath = LineString.fromLngLats(flightPathGeo);
        allFeatures.add(Feature.fromGeometry(flightPath));

        FeatureCollection features = FeatureCollection.fromFeatures(allFeatures);
        try {
            this.file.write(features.toJson());
            this.file.close();
        } catch (Exception e) {
            System.out.println("ERROR CLOSING READINGLOGGER");
        }
    }

    private void updateMarkerProps(Sensor read_sensor) {
        Feature marker = this.markers.get(read_sensor.getLocation());
        MarkerProperties markerProps = (read_sensor.getBattery() < MIN_BATTERY) ?
                MarkerProperties.LOWBATTERY :
                MarkerProperties.fromAirPollution(read_sensor.getReading());

        marker.removeProperty("marker-color");
        marker.addStringProperty("marker-color", markerProps.getRgbString());
        marker.addStringProperty("marker-symbol", markerProps.getMarkerSymbol());
    }
}
