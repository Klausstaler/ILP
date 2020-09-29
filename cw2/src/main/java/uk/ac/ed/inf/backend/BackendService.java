package uk.ac.ed.inf.backend;

import com.google.gson.Gson;

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
        System.out.println("BackendService initializing with " + url);

        this.setupNewUrl(String.format("%s:%s/",url, port));
        this.baseUrl = new URL(String.format("%s:%s/",url, port));

        System.out.println("Finished initialization of BackendService.");
    }

    protected void setupNewUrl(String url) throws IOException {
        this.url = new URL(url);
        this.connection =  (HttpURLConnection) this.url.openConnection();
        this.connection.setRequestMethod("GET");
    }

    protected String readResponse() throws IOException {
        if ((this.connection.getResponseCode() == 400) || (this.connection.getResponseCode() == 500)) {
            throw new IOException("Illegal response code!");
        }

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
}
