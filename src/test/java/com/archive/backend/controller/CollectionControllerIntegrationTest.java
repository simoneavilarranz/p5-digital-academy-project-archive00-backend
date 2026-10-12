package com.archive.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.archive.backend.dto.CollectionRequest;
import com.archive.backend.dto.CollectionResponse;
import com.archive.backend.dto.ReviewRequest;
import com.archive.backend.entity.CollectionType;
import com.archive.backend.service.CollectionService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CollectionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CollectionService collectionService;

    @Test
    @WithMockUser(username = "testuser")
    void addToCollection_shouldReturnCreated() throws Exception {
        CollectionResponse response = new CollectionResponse(
            1L, "Radiohead/OK Computer", "OK Computer", "Radiohead",
            "img.png", CollectionType.SAVED, 5, "Great", LocalDateTime.now()
        );

        when(collectionService.addToCollection(any(CollectionRequest.class), eq("testuser")))
            .thenReturn(response);

        String body = """
            {
                "lastFmId": "Radiohead/OK Computer",
                "albumName": "OK Computer",
                "artistName": "Radiohead",
                "coverUrl": "img.png",
                "type": "SAVED",
                "rating": 5,
                "reviewText": "Great"
            }
        """;

        mockMvc.perform(post("/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.albumName").value("OK Computer"))
            .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getMyCollection_shouldReturnList() throws Exception {
        CollectionResponse response = new CollectionResponse(
            1L, "Radiohead/OK Computer", "OK Computer", "Radiohead",
            "img.png", CollectionType.SAVED, 5, "Great", LocalDateTime.now()
        );

        when(collectionService.getUserCollection(eq("testuser"), any()))
            .thenReturn(List.of(response));

        mockMvc.perform(get("/collections/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].albumName").value("OK Computer"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void removeFromCollection_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/collections/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateReview_shouldReturnUpdated() throws Exception {
        CollectionResponse response = new CollectionResponse(
            1L, "Radiohead/OK Computer", "OK Computer", "Radiohead",
            "img.png", CollectionType.SAVED, 5, "Updated review", LocalDateTime.now()
        );

        when(collectionService.updateReview(eq(1L), eq("testuser"), any(ReviewRequest.class)))
            .thenReturn(response);

        String body = """
            {
                "reviewText": "Updated review"
            }
        """;

        mockMvc.perform(put("/collections/1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reviewText").value("Updated review"));
    }

}