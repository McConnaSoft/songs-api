package com.mcconasoft.songs_api.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(name = "CreateSongRequest")
public record CreateSongRequest(
        @Schema(description = "Artist ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull(message = "artistId is required")
        UUID artistId,

        @Schema(example = "Under the Bridge")
        @NotBlank(message = "title is required")
        @Size(max = 255, message = "title must be at most 255 characters")
        String title,

        @Schema(example = "E")
        @Size(max = 50, message = "musicalKey must be at most 50 characters")
        String musicalKey,

        @Schema(example = "84")
        @Positive(message = "bpm must be greater than 0")
        Integer bpm,

        @Schema(example = "Practice intro arpeggio slowly; focus on clean transitions.")
        String notes
) { }
