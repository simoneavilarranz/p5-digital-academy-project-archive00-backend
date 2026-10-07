package com.archive.backend.dto.lastfm;

public record ArtistDetails(
    String name,
    String imageUrl,
    String lastFmUrl,
    String bio,
    Long listeners,
    Long playcount
) {}
