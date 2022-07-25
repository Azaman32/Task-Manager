package main.java.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String url;
    private String token;

    public KVTaskClient(String url) {
        this.url = url;
    }

    public void put(String key, String json) {
        URI uri = URI.create(url + "/save/"+ key + "?API_TOKEN="+ getToken());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            client.send(request,handler);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public String load(String key){
        String tasks = null;
        URI uri = URI.create(url + "/load/"+ key + "?API_TOKEN="+ getToken());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            HttpResponse<String> response = client.send(request, handler);
            tasks = response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    private String getToken() {
        if (token==null) {
            URI uri = URI.create(url + "/register");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            try {
                HttpResponse<String> response = client.send(request, handler);
                token = response.body();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return token;
    }
}
