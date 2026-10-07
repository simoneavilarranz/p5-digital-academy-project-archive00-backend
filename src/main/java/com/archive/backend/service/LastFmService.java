package com.archive.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.archive.backend.dto.lastfm.AlbumSummary;
import com.archive.backend.dto.lastfm.SearchResponse;

import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

@Service 
@RequiredArgsConstructor 
public class LastFmService {
    
    @Value("${lastfm.api.key}")
    private String apiKey;

    @Value ("${lastfm.api.url}")
    private String apiUrl;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Cacheable("search") 
    public SearchResponse search(String query) {
        String response = restClient.get()
        .uri(apiUrl + "?method=album.search&album={query}&api_key={key}&format=json", query, apiKey)
        .retrieve()
        .body(String.class);

                try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode albumsNode = root.path("results").path("albummatches").path("album");

            List<AlbumSummary> albums = new ArrayList<>();

            for (JsonNode albumNode : albumsNode) {
                String name = albumNode.path("name").asText();
                String artist = albumNode.path("artist").asText();
                String url = albumNode.path("url").asText();
                String imageUrl = extractImageUrl(albumNode);

                albums.add(new AlbumSummary(name, artist, imageUrl, url));
            }

            return new SearchResponse(albums, List.of());
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Last.fm response", e);
        }
    }

    private String extractImageUrl(JsonNode node) {
        JsonNode images = node.path("image");
        for (JsonNode image : images) {
            if ("extralarge".equals(image.path("size").asText())) {
                return image.path("#text").asText();
            }
        }
        return "";
    }
}
