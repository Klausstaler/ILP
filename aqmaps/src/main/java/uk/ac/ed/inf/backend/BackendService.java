package uk.ac.ed.inf.backend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


/**
 * Service used to handle the connection with a webserver.
 */
public class BackendService {

    protected final String baseUrl;
    private HttpClient client = HttpClient.newHttpClient();

    public BackendService(String url, String port) {
        System.out.println("BackendService initializing with " + url);

        this.baseUrl = String.format("%s:%s/", url, port);
        System.out.println("Finished initialization of BackendService.");
    }

    /**
     * Reads the response from the current connection.
     * @return A String representing whatever we got back from the current connection.
     * @throws IOException
     * @param url
     */
    protected String getResponse(String url) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        var response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Illegal response code " + response.statusCode() + ", exiting " +
                    "application.");
        }
        return response.body();
    }
}
