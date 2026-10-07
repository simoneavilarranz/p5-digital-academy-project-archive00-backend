package com.archive.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.archive.backend.dto.lastfm.AlbumDetails;
import com.archive.backend.dto.lastfm.AlbumSummary;
import com.archive.backend.dto.lastfm.ArtistDetails;
import com.archive.backend.dto.lastfm.ArtistSummary;
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

            List<ArtistSummary> artists = searchArtists(query);
            return new SearchResponse(albums, artists);
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

    private List<ArtistSummary> searchArtists(String query) {
    String response = restClient.get()
        .uri(apiUrl + "?method=artist.search&artist={query}&api_key={key}&format=json", query, apiKey)
        .retrieve()
        .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode artistsNode = root.path("results").path("artistmatches").path("artist");

            List<ArtistSummary> artists = new ArrayList<>();

            for (JsonNode artistNode : artistsNode) {
                String name = artistNode.path("name").asText();
                String url = artistNode.path("url").asText();
                String imageUrl = extractImageUrl(artistNode);

                artists.add(new ArtistSummary(name, imageUrl, url));
            }

            return artists;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Last.fm artist search response", e);
        }
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

            List<TrackInfo> tracks = extractTracks(albumNode);

            return new AlbumDetails(name, artistName, imageUrl, url, description, tracks);
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

    @Cacheable("artists")
    public ArtistDetails getArtistDetails(String artist) {
        String response = restClient.get()
        .uri(apiUrl + "?method=artist.getinfo&artist={artist}&api_key={key}&format=json",
            artist, apiKey)
        .retrieve()
        .body(String.class);
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode artistNode = root.path("artist");

            String name = artistNode.path("name").asText();
            String url = artistNode.path("url").asText();
            String imageUrl = extractImageUrl(artistNode);
            String bio = artistNode.path("bio").path("summary").asText();

            long listeners = 0;
            long playcount = 0;
            try {
                listeners = Long.parseLong(artistNode.path("stats").path("listeners").asText());
                playcount = Long.parseLong(artistNode.path("stats").path("playcount").asText());
            } catch (NumberFormatException e) {
            }

            return new ArtistDetails(name, imageUrl, url, bio, listeners, playcount);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Last.fm artist response", e);
        }
    }
}
