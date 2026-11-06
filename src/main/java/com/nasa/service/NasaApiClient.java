package com.nasa.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nasa.model.ApodResponse;
import com.nasa.model.EpicResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class NasaApiClient {
    private static final String API_KEY = "DEMO_KEY";
    private final HttpClient httpClient;
    private final Gson gson;

    public NasaApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    public ApodResponse fetchApod(String date) throws Exception {
        // Monta o endereço com a URL da API e a data que o user vai informar
        String url = "https://api.nasa.gov/planetary/apod?api_key=" + API_KEY + "&date=" + date;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro ao consultar APOD: Código " + response.statusCode());
        }
        return gson.fromJson(response.body(), ApodResponse.class);
    }

    public EpicResponse fetchEpic(String date) throws Exception {
        // Monta o endereço com a URL da API e a data que o user vai informar
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
}
