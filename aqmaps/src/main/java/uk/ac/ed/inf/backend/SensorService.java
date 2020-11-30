package uk.ac.ed.inf.backend;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.locationtech.jts.geom.Coordinate;
import uk.ac.ed.inf.Sensor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Service responsible for pulling the sensors from the webserver.
 */
public class SensorService extends BackendService {

    private static final double MIN_BATTERY = 10.0; // minimum battery required for reading to be
    // valid
    private final Gson gson = new Gson();
    private HashMap<String, Sensor> sensors; // key is the what3words address
    // of the sensor value


    public SensorService(String url, String port, String day, String month, String year) throws IOException, InterruptedException {
        super(url, port);

        var sensorUrl = (
                String.format("%smaps/%s/%s/%s/air-quality-data.json",
                        this.baseUrl,
                        year, month, day));
        this.sensors = this.retrieveAllSensors(sensorUrl);
    }

    public List<Sensor> getSensors() {
        return new ArrayList<>(this.sensors.values());
    }


    /**
     * Retrieves all sensors from the webserver for the current url.
     * @return A HashMap, where the key is the what3words string of the sensor which is the value.
     * @throws IOException
     */
    private HashMap<String, Sensor> retrieveAllSensors(String url) throws IOException,
            InterruptedException {
        HashMap<String, Sensor> sensors = new HashMap<>();
        System.out.println("Retrieving all sensors...");
        var response = this.gson.fromJson(this.getResponse(url), JsonArray.class);
        for (JsonElement rawSensor : response) {
            Sensor sensor = this.jsonToSensor(rawSensor);
            sensors.put(sensor.getLocation(), sensor);
        }
        System.out.println("All sensors retrieved!");
        return sensors;
    }

    /**
     * Turns a JsonElement into a Sensor object.
     * @param rawSensor Is the JsonElement which contains the necessary properties needed to
     *                  initialize a Sensor. (location, battery, reading)
     * @return A Sensor object.
     * @throws IOException
     */
    private Sensor jsonToSensor(JsonElement rawSensor) throws IOException, InterruptedException {
        var sensorProperties = rawSensor.getAsJsonObject();
        var sensorLocation = sensorProperties.get("location").getAsString();
        var coordinate = this.getCoordinate(sensorLocation); // get location information

        Double reading = null;
        float battery = sensorProperties.get("battery").getAsFloat();
        if (battery > MIN_BATTERY) { // invalidate reading if below threshold
            String readingVal = sensorProperties.get("reading").getAsString();
            reading = Double.valueOf(readingVal);
        }

        return new Sensor(sensorLocation, battery, reading, coordinate.x, coordinate.y);
    }

    /**
     * Gets the location information of a what3words address.
     * @param sensorLocation The what3words address to get the location information for.
     * @return A coordinate, where x is the longitude and y is the latitude of the location.
     * @throws IOException
     */
    private Coordinate getCoordinate(String sensorLocation) throws IOException,
            InterruptedException {
        String[] words = sensorLocation.split("\\.");

        var locationUrl = String.format(
                "%swords/%s/%s/%s/details.json", this.baseUrl, words[0], words[1],
                words[2]);

        var response = this.gson.fromJson(this.getResponse(locationUrl), JsonObject.class);
        var coords = response.get("coordinates").getAsJsonObject();
        var coordinate = new Coordinate(
                coords.get("lng").getAsDouble(),
                coords.get("lat").getAsDouble());

        return coordinate;
    }
}
