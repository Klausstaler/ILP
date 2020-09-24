package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReadingLogger extends DroneLogger {

    private List<Point> flightPath = new ArrayList<>();
    private HashMap<String, Feature> markers;

    public ReadingLogger(Point initialPos, String date) throws IOException {
        super(initialPos, "readings"+date+".geojson");
        this.flightPath.add(initialPos);

    }

    @Override
    public void log(Point newPos, Sensor read_sensor) {
        this.flightPath.add(newPos);
        this.position = newPos;
        if (read_sensor != null) {
            this.updateMarkerProps(read_sensor);
        }
    }

    @Override
    public void close() throws IOException {
        // TODO: Write flight path and markers
        this.file.close();
    }

    private void updateMarkerProps(Sensor read_sensor) {
        Feature marker = this.markers.get(read_sensor.getLocation());
        MarkerProperties markerProps =(read_sensor.getBattery() < 10) ?
                MarkerProperties.from("lowBattery") :
                MarkerProperties.fromAirPollution(read_sensor.getReading());
        marker.removeProperty("marker-color");
        marker.addStringProperty("marker-color", markerProps.getRgbString());
        marker.addStringProperty("marker-symbol", markerProps.getMarkerSymbol());
    }
}
