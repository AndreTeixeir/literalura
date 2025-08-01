package io.github.andreteixeir.literalura.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Service class responsible for consuming an external API.
 * This class provides a method to fetch data from a given URL.
 */
public class ApiConsumer {

    /**
     * Fetches data from the specified URL.
     *
     * @param url The URL of the API endpoint to fetch data from.
     * @return A String containing the body of the HTTP response, typically a JSON.
     */
    public String fetchData(String url) {
        // Create a new HttpClient with default settings.
        HttpClient client = HttpClient.newHttpClient();

        // Build a new HttpRequest for the given URL.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = null;
        try {
            // Send the request and get the response.
            // The response body is handled as a String.
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            // Handle exceptions related to I/O errors (e.g., network issues).
            System.err.println("Error during HTTP request (I/O): " + e.getMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            // Handle exceptions if the operation is interrupted.
            System.err.println("HTTP request was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore the interrupted status
            throw new RuntimeException(e);
        }

        // Return the body of the response.
        return response.body();
    }
}