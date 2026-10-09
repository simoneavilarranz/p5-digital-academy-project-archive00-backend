package com.archive.backend.dto.lastfm;

import java.util.List;

public record ArtistDetails(
    String name,
    String imageUrl,
    String lastFmUrl,
    String bio,
    List<AlbumSummary> topAlbums
) {}