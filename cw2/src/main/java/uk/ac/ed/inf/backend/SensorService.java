package uk.ac.ed.inf.backend;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.locationtech.jts.geom.Coordinate;
import uk.ac.ed.inf.Sensor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SensorService extends BackendService {

    private static final double MIN_BATTERY = 10.0;

    private HashMap<String, Sensor> sensors = new HashMap<>();

    public SensorService(String url, String port) throws IOException {
        super(url, port);
        this.setupNewUrl(this.url.toString() + "maps/");
    }

    public SensorService(String url, String port, String day, String month, String year) throws IOException {
        this(url, port);
        this.setupNewUrl(
                String.format("%s/%s/%s/%s/air-quality-data.json", this.url.toString(),
                        year, month, day));
        this.sensors = this.retrieveAllSensors();
    }

    private HashMap<String, Sensor> retrieveAllSensors() throws IOException {
        HashMap<String, Sensor> sensors = new HashMap<>();
        System.out.println("Retrieving all sensors...");
        JsonArray response = this.gson.fromJson(this.readResponse(), JsonArray.class);
        for (JsonElement rawSensor : response) {
            Sensor sensor = this.JsonToSensor(rawSensor);
            sensors.put(sensor.getLocation(), sensor);
        }
        System.out.println("All sensors retrieved!");
        return sensors;
    }

    private Sensor JsonToSensor(JsonElement rawSensor) throws IOException {
        JsonObject sensorProperties = rawSensor.getAsJsonObject();
        String location = sensorProperties.get("location").getAsString();
        Coordinate coordinate = this.getCoordinate(sensorProperties);
        Double reading = null;
        double battery = sensorProperties.get("battery").getAsDouble();

        if (battery > MIN_BATTERY) {
            String readingVal = sensorProperties.get("reading").getAsString();
            reading = Double.valueOf(readingVal);
        }

        Sensor sensor = new Sensor(location, battery, reading, coordinate.x, coordinate.y);
        return sensor;
    }

    private Coordinate getCoordinate(JsonObject sensorProperties) throws IOException {
        String[] location = sensorProperties.get("location").getAsString().split("\\.");
        String prevUrl = this.url.toString();

        this.setupNewUrl(String.format(
                "%s/words/%s/%s/%s/details.json", this.baseUrl, location[0], location[1],
                location[2]));

        JsonObject response = this.gson.fromJson(this.readResponse(), JsonObject.class);
        JsonObject coords = response.get("coordinates").getAsJsonObject();
        Coordinate coordinate = new Coordinate(
                coords.get("lng").getAsDouble(),
                coords.get("lat").getAsDouble());

        this.setupNewUrl(prevUrl);
        return coordinate;
    }

    public List<Sensor> getSensors() {
        return new ArrayList<Sensor>(this.sensors.values());
    }


    //public setDate
}
