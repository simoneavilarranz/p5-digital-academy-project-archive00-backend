package com.archive.backend.dto.lastfm;

import java.util.List;

public record AlbumDetails(
    String name,
    String artist,
    String imageUrl,
    String lastFmUrl,
    String description,
    List<TrackInfo> tracks
) {}
