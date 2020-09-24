package uk.ac.ed.inf;


import com.mapbox.geojson.Point;
import uk.ac.ed.inf.backend.BackendService;
import uk.ac.ed.inf.backend.SensorService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class App 
{
    public static void main( String[] args ) throws IOException {

        Point pos = Point.fromLngLat(55.946233, -3.192473);
        DroneLogger logger = new CombinedLogger(pos, "02-02-2021");

        // "http://localhost:80/maps/2021/02/02/airqualitydata.json"

        URL url = new URL("http://localhost:80/maps/2021/02/02/air-quality-data.json");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        System.out.println(con.getResponseCode());
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine = in.readLine();
        StringBuffer content = new StringBuffer();
        while ( inputLine != null) {
            content.append(inputLine);
            inputLine = in.readLine();
        }
        System.out.println(content.toString());
        in.close();

        BackendService service = new SensorService(
                "http://localhost", "80", "02", "02", "2021");

// the response:
    }
}
