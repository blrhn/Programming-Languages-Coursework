package pwr.edu.travels.client.service.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {
    private static final String apiUrl = "https://api-dbw.stat.gov.pl/api/1.1.0/";

    // sends the request to the api endpoint and returns its response body if the status is 200, returns null otherwise
    public String get(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl + url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
