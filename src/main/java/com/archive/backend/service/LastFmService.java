package com.archive.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.archive.backend.dto.lastfm.AlbumDetails;
import com.archive.backend.dto.lastfm.AlbumSummary;
import com.archive.backend.dto.lastfm.SearchResponse;
import com.archive.backend.dto.lastfm.TrackInfo;

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

    @Cacheable("albums")
    public AlbumDetails getAlbumDetails(String artist, String album) {
        String response = restClient.get()
        .uri(apiUrl + "?method=album.getinfo&artist={artist}&album={album}&api_key={key}&format=json",
            artist, album, apiKey)
        .retrieve()
        .body(String.class);
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode albumNode = root.path("album");

            String name = albumNode.path("name").asText();
            String artistName = albumNode.path("artist").asText();
            String url = albumNode.path("url").asText();
            String imageUrl = extractImageUrl(albumNode);

            String description = albumNode.path("wiki").path("summary").asText();
            String releaseDate = albumNode.path("wiki").path("published").asText();

            List<TrackInfo> tracks = extractTracks(albumNode);

            return new AlbumDetails(name, artistName, imageUrl, url, description, releaseDate, tracks);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Last.fm album response", e);
        }
    }

    private List<TrackInfo> extractTracks(JsonNode albumNode) {
        JsonNode tracksNode = albumNode.path("tracks").path("track");
        List<TrackInfo> tracks = new ArrayList<>();

        if (tracksNode.isArray()) {
            for (JsonNode trackNode : tracksNode) {
                tracks.add(buildTrackInfo(trackNode));
            }
        } else if (tracksNode.isObject()) {
            tracks.add(buildTrackInfo(tracksNode));
        }

        return tracks;
    }

    private TrackInfo buildTrackInfo(JsonNode trackNode) {
        String name = trackNode.path("name").asText();
        int duration = trackNode.path("duration").asInt();
        int position = trackNode.path("@attr").path("rank").asInt();
        return new TrackInfo(name, duration, position);
    }
}
