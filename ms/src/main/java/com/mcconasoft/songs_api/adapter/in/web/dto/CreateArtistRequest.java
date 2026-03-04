package com.mcconasoft.songs_api.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "CreateArtistRequest")
public record CreateArtistRequest(
        @Schema(example = "Red Hot Chili Peppers")
        @NotBlank(message = "name is required")
        @Size(max = 255, message = "name must be at most 255 characters")
        String name
) { }
