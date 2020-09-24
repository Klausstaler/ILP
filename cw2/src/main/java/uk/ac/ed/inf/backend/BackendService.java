package uk.ac.ed.inf.backend;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackendService {

    protected URL url;
    protected URL baseUrl;
    protected Gson gson = new Gson();
    protected HttpURLConnection connection;

    public BackendService(String url, String port) throws IOException {
        //http://localhost/maps/2020/02/02/
        this.setupNewUrl(String.format("%s:%s/",url, port));
        this.baseUrl = new URL(String.format("%s:%s/",url, port));
    }

    protected void setupNewUrl(String url) throws IOException {
        if (this.url != null) {
            System.out.println(String.format("BackendService switches from %s to %s",
                    this.url.toString(), url));
        }
        else {
                System.out.println("BackendService initializing with " + url);
        }
        this.url = new URL(url);
        this.connection =  (HttpURLConnection) this.url.openConnection();
        this.connection.setRequestMethod("GET");
    }

    protected String readResponse() throws IOException {
        if ((this.connection.getResponseCode() == 400) || (this.connection.getResponseCode() == 500)) {
            throw new IOException("Illegal response code!");
        }
        System.out.println("Reading response from " + this.url.toString());
        System.out.println("Response code " + this.connection.getResponseCode());

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(this.connection.getInputStream()));

        String inputLine = reader.readLine();
        StringBuilder content = new StringBuilder();

        while (inputLine != null) {
            content.append(inputLine);
            inputLine = reader.readLine();
        }
        reader.close();
        System.out.println(content.toString());
        return content.toString();
    }
}
