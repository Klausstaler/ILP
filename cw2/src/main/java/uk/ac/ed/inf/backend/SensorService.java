package uk.ac.ed.inf.backend;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import uk.ac.ed.inf.Sensor;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class SensorService extends BackendService {

    private HashSet<Sensor> sensors = new HashSet<>();

    public SensorService(String url, String port) throws IOException {
        super(url, port);
        this.baseUrl = new URL(this.baseUrl.toString() + "maps/");
    }

    public SensorService(String url, String port, String day, String month, String year) throws IOException {
        this(url, port);
        this.setupNewUrl(
                String.format("%s/%s/%s/%s/air-quality-data.json", this.baseUrl.toString(),
                        year, month, day));
        this.sensors = this.getAllSensors();
    }

    private HashSet<Sensor> getAllSensors() throws IOException {
        JsonPrimitive response = this.readResponseAsPrim();
        System.out.println(response.toString());
        return null;
    }


    //public setDate
}
