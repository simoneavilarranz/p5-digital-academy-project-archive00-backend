package com.archive.backend.dto;

import java.time.LocalDateTime;

import com.archive.backend.entity.CollectionType;

public record CollectionResponse(
    Long id,
    String lastFmId,
    String albumName,
    String artistName,
    String coverUrl,
    CollectionType type,
    Integer rating,
    String reviewText,
    LocalDateTime createdAt
) {}