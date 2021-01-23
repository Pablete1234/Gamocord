package me.pablete1234.gamocord;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GamocosmServer {

    private static final String API_URL = "https://gamocosm.com/servers/{server_id}/api/{api_key}/";
    private final String name;
    private final String serverId;
    private final String apiKey;
    private final HttpClient client;

    public GamocosmServer(String name, String serverId, String apiKey) {
        this.name = name;
        this.serverId = serverId;
        this.apiKey = apiKey;
        this.client = HttpClient.newHttpClient();
    }

    public String getName() {
        return name;
    }

    private URI getURI(String endpoint) {
        return URI.create((API_URL + endpoint).replace("{server_id}", serverId).replace("{api_key}", apiKey));
    }

    private String get(String endpoint) throws IOException, InterruptedException {
        try {
            return client.send(
                    HttpRequest.newBuilder().uri(getURI(endpoint)).build(),
                    HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            // For some odd reason every so often we get connection closed exceptions, just a simple quick & dirty retry
            return client.send(
                    HttpRequest.newBuilder().uri(getURI(endpoint)).build(),
                    HttpResponse.BodyHandlers.ofString()).body();
        }
    }

    private String post(String endpoint, String data) throws IOException, InterruptedException {
        try {
            return client.send(
                    HttpRequest.newBuilder().uri(getURI(endpoint)).POST(HttpRequest.BodyPublishers.ofString(data)).build(),
                    HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            // For some odd reason every so often we get connection closed exceptions, just a simple quick & dirty retry
            return client.send(
                    HttpRequest.newBuilder().uri(getURI(endpoint)).POST(HttpRequest.BodyPublishers.ofString(data)).build(),
                    HttpResponse.BodyHandlers.ofString()).body();
        }
    }

    public String getStatus() throws Exception {
        return get("status");
    }

    public String start() throws Exception {
        return post("start", "");
    }

    public String stop() throws Exception {
        return post("stop", "");
    }

    public String reboot() throws Exception {
        return post("reboot", "");
    }

    public String pause() throws Exception {
        return post("pause", "");
    }

    public String resume() throws Exception {
        return post("resume", "");
    }

}
