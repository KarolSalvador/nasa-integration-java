package com.nasa.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.nasa.model.EpicResponse;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
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

    // Novo método para buscar Galáxias/Astros na Biblioteca da NASA (Garantido carregar imagens!)
    public NasaImageResult searchSpaceObject(String query) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://images-api.nasa.gov/search?q=" + encodedQuery + "&media_type=image";
        
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

        // Pega o primeiro resultado válido
        JsonObject firstItem = items.get(0).getAsJsonObject();
        JsonObject data = firstItem.getAsJsonArray("data").get(0).getAsJsonObject();
        
        String title = data.get("title").getAsString();
        String description = data.get("description").getAsString();
        String dateCreated = data.get("date_created").getAsString().substring(0, 10);

        JsonArray links = firstItem.getAsJsonArray("links");
        String imgUrl = links.get(0).getAsJsonObject().get("href").getAsString();

        return new NasaImageResult(title + " (" + dateCreated + ")", description, imgUrl);
    }

    public EpicResponse fetchEpic(String date) throws Exception {
        String url = "https://api.nasa.gov/EPIC/api/natural/date/" + date + "?api_key=" + API_KEY;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro ao consultar EPIC: Código " + response.statusCode());
        }
        
        List<EpicResponse> list = gson.fromJson(response.body(), new TypeToken<List<EpicResponse>>(){}.getType());
        if (list == null || list.isEmpty()) {
            throw new RuntimeException("Nenhuma imagem EPIC encontrada para esta data.");
        }
        return list.get(0);
    }

    // Classe auxiliar interna para o resultado da busca de imagens
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