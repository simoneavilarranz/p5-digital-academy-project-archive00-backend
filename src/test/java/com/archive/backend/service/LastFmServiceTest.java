package com.archive.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.archive.backend.dto.lastfm.AlbumDetails;
import com.archive.backend.dto.lastfm.ArtistDetails;
import com.archive.backend.dto.lastfm.SearchResponse;

import tools.jackson.databind.ObjectMapper;

public class LastFmServiceTest {

    private LastFmService lastFmService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();

        ObjectMapper objectMapper = new ObjectMapper();

        lastFmService = new LastFmService(restClient, objectMapper);
        ReflectionTestUtils.setField(lastFmService, "apiKey", "test-key");
        ReflectionTestUtils.setField(lastFmService, "apiUrl", "http://test.last.fm/");
    }

    @Test
    void search_shouldParseAlbumAndArtistResponse() {
        String albumJson = """
            {
                "results": {
                    "albummatches": {
                        "album": [
                            {
                                "name": "OK Computer",
                                "artist": "Radiohead",
                                "url": "https://last.fm/okc",
                                "image": [
                                    { "#text": "small.png", "size": "small" },
                                    { "#text": "large.png", "size": "extralarge" }
                                ]
                            }
                        ]
                    }
                }
            }
            """;

        String artistJson = """
            {
                "results": {
                    "artistmatches": {
                        "artist": [
                            {
                                "name": "Radiohead",
                                "url": "https://last.fm/radiohead",
                                "image": [
                                    { "#text": "artist.png", "size": "extralarge" }
                                ]
                            }
                        ]
                    }
                }
            }
            """;

        mockServer.expect(requestTo(containsString("album.search")))
            .andRespond(withSuccess(albumJson, MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(containsString("artist.search")))
            .andRespond(withSuccess(artistJson, MediaType.APPLICATION_JSON));

        SearchResponse response = lastFmService.search("radiohead");

        assertThat(response.albums()).hasSize(1);
        assertThat(response.albums().get(0).name()).isEqualTo("OK Computer");
        assertThat(response.albums().get(0).imageUrl()).isEqualTo("large.png");
        assertThat(response.artists()).hasSize(1);
        assertThat(response.artists().get(0).name()).isEqualTo("Radiohead");
    }

    @Test
    void getAlbumDetails_shouldParseResponse() {
        String json = """
            {
                "album": {
                    "name": "OK Computer",
                    "artist": "Radiohead",
                    "url": "https://last.fm/okc",
                    "image": [
                        { "#text": "cover.png", "size": "extralarge" }
                    ],
                    "wiki": {
                        "summary": "A description of the album."
                    },
                    "tracks": {
                        "track": [
                            { "name": "Airbag", "duration": 284, "@attr": { "rank": 1 } },
                            { "name": "Paranoid Android", "duration": 384, "@attr": { "rank": 2 } }
                        ]
                    }
                }
            }
            """;

        mockServer.expect(requestTo(containsString("album.getinfo")))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        AlbumDetails details = lastFmService.getAlbumDetails("Radiohead", "OK Computer");

        assertThat(details.name()).isEqualTo("OK Computer");
        assertThat(details.artist()).isEqualTo("Radiohead");
        assertThat(details.imageUrl()).isEqualTo("cover.png");
        assertThat(details.description()).isEqualTo("A description of the album.");
        assertThat(details.tracks()).hasSize(2);
        assertThat(details.tracks().get(0).name()).isEqualTo("Airbag");
        assertThat(details.tracks().get(0).duration()).isEqualTo(284);
        assertThat(details.tracks().get(0).position()).isEqualTo(1);
    }

    @Test
    void getArtistDetails_shouldParseResponseWithTopAlbums() {
        String artistJson = """
            {
                "artist": {
                    "name": "Radiohead",
                    "url": "https://last.fm/radiohead",
                    "image": [
                        { "#text": "artist.png", "size": "extralarge" }
                    ],
                    "bio": {
                        "summary": "British rock band."
                    }
                }
            }
            """;

        String albumsJson = """
            {
                "topalbums": {
                    "album": [
                        {
                            "name": "OK Computer",
                            "artist": { "name": "Radiohead" },
                            "url": "https://last.fm/okc",
                            "image": [
                                { "#text": "okc.png", "size": "extralarge" }
                            ]
                        }
                    ]
                }
            }
            """;

        mockServer.expect(requestTo(containsString("artist.getinfo")))
            .andRespond(withSuccess(artistJson, MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(containsString("artist.gettopalbums")))
            .andRespond(withSuccess(albumsJson, MediaType.APPLICATION_JSON));

        ArtistDetails details = lastFmService.getArtistDetails("Radiohead");

        assertThat(details.name()).isEqualTo("Radiohead");
        assertThat(details.imageUrl()).isEqualTo("artist.png");
        assertThat(details.bio()).isEqualTo("British rock band.");
        assertThat(details.topAlbums()).hasSize(1);
        assertThat(details.topAlbums().get(0).name()).isEqualTo("OK Computer");
    }

}