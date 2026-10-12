package com.archive.backend.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.archive.backend.dto.lastfm.AlbumDetails;
import com.archive.backend.dto.lastfm.AlbumSummary;
import com.archive.backend.dto.lastfm.ArtistDetails;
import com.archive.backend.dto.lastfm.SearchResponse;
import com.archive.backend.dto.lastfm.TrackInfo;
import com.archive.backend.service.LastFmService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CatalogControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LastFmService lastFmService;

        @Test
    void search_shouldReturnSearchResponse() throws Exception {
        SearchResponse response = new SearchResponse(
            List.of(new AlbumSummary("OK Computer", "Radiohead", "img.png", "url")),
            List.of()
        );

        when(lastFmService.search("radiohead")).thenReturn(response);

        mockMvc.perform(get("/catalog/search").param("q", "radiohead"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.albums[0].name").value("OK Computer"));
    }

    @Test
    void getAlbumDetails_shouldReturnAlbumDetails() throws Exception {
        AlbumDetails details = new AlbumDetails(
            "OK Computer", "Radiohead", "img.png", "url", "desc",
            List.of(new TrackInfo("Airbag", 284, 1))
        );

        when(lastFmService.getAlbumDetails("Radiohead", "OK Computer")).thenReturn(details);

        mockMvc.perform(get("/catalog/album/Radiohead/OK Computer"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("OK Computer"))
            .andExpect(jsonPath("$.tracks[0].name").value("Airbag"));
    }

    @Test
    void getArtistDetails_shouldReturnArtistDetails() throws Exception {
        ArtistDetails details = new ArtistDetails(
            "Radiohead", "img.png", "url", "bio",
            List.of(new AlbumSummary("OK Computer", "Radiohead", "img.png", "url"))
        );

        when(lastFmService.getArtistDetails("Radiohead")).thenReturn(details);

        mockMvc.perform(get("/catalog/artist/Radiohead"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Radiohead"))
            .andExpect(jsonPath("$.topAlbums[0].name").value("OK Computer"));
    }

}