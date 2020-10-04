package uk.ac.ed.inf.backend;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import uk.ac.ed.inf.Restrictions;
import uk.ac.ed.inf.Sensor;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public class SensorService extends BackendService {

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
            System.out.println(sensor);
        }
        System.out.println("All sensors retrieved!");
        return sensors;
    }

    private Sensor JsonToSensor(JsonElement rawSensor) throws IOException {
        JsonObject sensorProperties = rawSensor.getAsJsonObject();
        if (sensorProperties.get("battery").getAsInt() < Restrictions.MIN_BATTERY.getValue()) {
            sensorProperties.remove("reading");
        }
        Point coordinate = this.getCoordinates(sensorProperties);
        Sensor sensor =  this.gson.fromJson(sensorProperties, Sensor.class);
        sensor.setCoordinate(coordinate);
        return sensor;
    }

    private Point getCoordinates(JsonObject sensorProperties) throws IOException {
        String[] location = sensorProperties.get("location").getAsString().split("\\.");
        String prevUrl = this.url.toString();

        this.setupNewUrl(String.format(
                "%s/words/%s/%s/%s/details.json", this.baseUrl, location[0], location[1],
                location[2]));

        JsonObject response = this.gson.fromJson(this.readResponse(), JsonObject.class);
        JsonObject coords = response.get("coordinates").getAsJsonObject();
        Point coordinate = new GeometryFactory().createPoint(new Coordinate(
                coords.get("lng").getAsDouble(),
                coords.get("lat").getAsDouble()));
        //sensorProperties.add("coordinates", this.gson.toJsonTree(point, Point.class));

        this.setupNewUrl(prevUrl);
        return coordinate;
    }

    public Sensor sensorByLocation(String location) {
        return this.sensors.get(location);
    }

    public Collection<Sensor> getSensors() {
        return this.sensors.values();
    }


    //public setDate
}
