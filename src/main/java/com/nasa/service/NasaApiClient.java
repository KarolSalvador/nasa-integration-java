package com.nasa.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class NasaApiClient {
    private static final String API_KEY = resolveApiKey();

    private static String resolveApiKey() {
        String envKey = System.getenv("NASA_API_KEY");
        if (envKey != null && !envKey.isEmpty()) {
            return envKey;
        }
        return "DEMO_KEY";
    }

    private final HttpClient httpClient;
    private final Gson gson;

    public NasaApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    // Retorna até 20 resultados válidos da Biblioteca da NASA para galáxias e astros
    public List<NasaImageResult> searchSpaceObjects(String query) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://images-api.nasa.gov/search?q=" + encodedQuery + "&media_type=image&api_key=" + API_KEY;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro ao consultar Biblioteca NASA: Código " + response.statusCode());
        }

        JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
        JsonObject collection = jsonObject.getAsJsonObject("collection");
        JsonArray items = collection.getAsJsonArray("items");

        if (items == null || items.size() == 0) {
            throw new RuntimeException("Nenhum registro visual encontrado para: " + query);
        }

        List<NasaImageResult> results = new ArrayList<>();
        int limit = Math.min(items.size(), 20); // Pega até 20 imagens

        for (int i = 0; i < limit; i++) {
            try {
                JsonObject item = items.get(i).getAsJsonObject();
                JsonObject data = item.getAsJsonArray("data").get(0).getAsJsonObject();

                String title = data.has("title") ? data.get("title").getAsString() : "Sem Título";
                String description = data.has("description") ? data.get("description").getAsString() : "Sem descrição disponível.";
                String dateCreated = data.has("date_created") ? data.get("date_created").getAsString().substring(0, 10) : "";

                JsonArray links = item.getAsJsonArray("links");
                if (links != null && links.size() > 0) {
                    String imgUrl = links.get(0).getAsJsonObject().get("href").getAsString();
                    results.add(new NasaImageResult(title + " (" + dateCreated + ")", description, imgUrl));
                }
            } catch (Exception ignored) {}
        }

        if (results.isEmpty()) {
            throw new RuntimeException("Nenhum registro visual válido encontrado para: " + query);
        }

        return results;
    }

    public static class NasaImageResult {
        private final String title;
        private final String description;
        private final String imageUrl;

        public NasaImageResult(String title, String description, String imageUrl) {
            this.title = title;
            this.description = description;
            this.imageUrl = imageUrl;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getImageUrl() { return imageUrl; }
    }
}