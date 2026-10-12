package com.archive.backend.dto.lastfm;

public record TrackInfo(
    String name,
    Integer duration,
    Integer position
) {}
