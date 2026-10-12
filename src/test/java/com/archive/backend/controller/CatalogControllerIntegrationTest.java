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
    
}