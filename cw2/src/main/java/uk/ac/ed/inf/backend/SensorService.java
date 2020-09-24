package uk.ac.ed.inf.backend;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import uk.ac.ed.inf.Sensor;

import java.io.IOException;
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
        JsonArray response = this.gson.fromJson(this.readResponse(), JsonArray.class);
        for (JsonElement rawSensor : response) {
            System.out.println(rawSensor);
            Sensor sensor = this.JsonToSensor(rawSensor);
        }
        System.out.println(sensors.values());
        return sensors;
    }

    private Sensor JsonToSensor(JsonElement rawSensor) {
        /*if (rawSensor.getAsJsonObject().get("battery").getAsInt() < 10) {
            rawSensor.getAsJsonObject().
        }
        Sensor sensor = this.gson.fromJson(rawSensor, Sensor.class);
        sensors.put(sensor.getLocation(), sensor);

         */
        return null;
    }


    //public setDate
}
