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

}