package com.archive.backend.dto;

import com.archive.backend.entity.CollectionType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CollectionRequest(
    @NotBlank String lastFmId,
    @NotBlank String albumName,
    @NotBlank String artistName,
    String coverUrl,
    @NotNull CollectionType type,
    @Min(1) @Max(5) Integer rating
) {}