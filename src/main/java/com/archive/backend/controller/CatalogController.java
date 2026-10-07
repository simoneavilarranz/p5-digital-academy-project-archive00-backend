package com.archive.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.archive.backend.dto.lastfm.AlbumDetails;
import com.archive.backend.dto.lastfm.SearchResponse;
import com.archive.backend.service.LastFmService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController 
@RequestMapping("/catalog")
@RequiredArgsConstructor 
public class CatalogController {
    
    private final LastFmService lastFmService;

    @GetMapping("/search")
    public SearchResponse search(@RequestParam String q) {
        return lastFmService.search(q);
    }

    @GetMapping("/album/{artist}/{album}")
    public AlbumDetails getAlbumDetails(
        @PathVariable String artist,
        @PathVariable String album
    ) {
        return lastFmService.getAlbumDetails(artist, album);
    }

}
