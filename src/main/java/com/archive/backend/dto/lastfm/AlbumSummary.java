package com.archive.backend.dto.lastfm;

public record AlbumSummary(
    String name,
    String artist,
    String imageUrl,
    String lastFmUrl
) {}
