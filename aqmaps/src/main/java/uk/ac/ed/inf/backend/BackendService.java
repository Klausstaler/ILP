package uk.ac.ed.inf.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Service used to handle the connection with a webserver.
 */
public class BackendService {

    protected final URL baseUrl;
    protected URL url;
    protected HttpURLConnection connection;

    public BackendService(String url, String port) throws IOException {
        System.out.println("BackendService initializing with " + url);

        this.setupNewUrl(String.format("%s:%s/", url, port));
        this.baseUrl = new URL(String.format("%s:%s/", url, port));

        System.out.println("Finished initialization of BackendService.");
    }

    /**
     * Sets up a new connection to the webserver with the given url.
     * @param url the url we want to navigate to
     * @throws IOException
     */
    protected void setupNewUrl(String url) throws IOException {
        this.url = new URL(url);
        this.connection = (HttpURLConnection) this.url.openConnection();
        this.connection.setRequestMethod("GET");
    }

    /**
     * Reads the response from the current connection.
     * @return A String representing whatever we got back from the current connection.
     * @throws IOException
     */
    protected String readResponse() throws IOException {
        if ((this.connection.getResponseCode() == 400) || (this.connection.getResponseCode() == 500)) {
            throw new IOException("Illegal response code!");
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(this.connection.getInputStream()));

        String inputLine = reader.readLine();
        StringBuilder content = new StringBuilder();

        while (inputLine != null) { // read all the content
            content.append(inputLine);
            inputLine = reader.readLine();
        }
        reader.close();
        return content.toString();
    }
}
