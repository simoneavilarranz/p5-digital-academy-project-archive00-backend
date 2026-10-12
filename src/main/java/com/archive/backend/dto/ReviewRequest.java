package com.archive.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
    @NotBlank @Size(max = 1000) String reviewText
) {}