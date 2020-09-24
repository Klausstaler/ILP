package uk.ac.ed.inf.backend;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackendService {

    protected URL baseUrl;
    protected HttpURLConnection connection;

    public BackendService(String url, String port) throws IOException {
        //http://localhost/maps/2020/02/02/
        this.setupNewUrl(String.format("%s:%s/",url, port));
        System.out.println("BackendService Connected to " + this.baseUrl.toString());
    }

    protected void setupNewUrl(String url) throws IOException {
        this.baseUrl = new URL(url);
        this.connection =  (HttpURLConnection) this.baseUrl.openConnection();
        this.connection.setRequestMethod("GET");
    }

    protected String readResponse() throws IOException {
        if ((this.connection.getResponseCode() == 400) || (this.connection.getResponseCode() == 500)) {
            throw new IOException("Illegal response code!");
        }
        System.out.println("Reading response from " + this.baseUrl.toString());
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(this.connection.getInputStream()));

        String inputLine = reader.readLine();
        StringBuilder content = new StringBuilder();

        while (inputLine != null) {
            content.append(inputLine);
            inputLine = reader.readLine();
        }
        reader.close();
        return content.toString();
    }

    protected JsonObject readResponseAsObj() throws IOException {
        return new Gson().fromJson(this.readResponse(), JsonObject.class);
    }

    protected JsonPrimitive readResponseAsPrim() throws IOException {
        System.out.println(this.readResponse());
        return new Gson().fromJson(this.readResponse(), JsonPrimitive.class);
    }
}
