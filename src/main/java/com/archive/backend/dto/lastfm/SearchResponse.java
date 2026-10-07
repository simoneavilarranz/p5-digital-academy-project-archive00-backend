package com.archive.backend.dto.lastfm;

import java.util.List;

public record SearchResponse(
    List<AlbumSummary> albums,
    List<ArtistSummary> artists
) {}
