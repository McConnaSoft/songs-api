package com.mcconasoft.songs_api.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "CreateSongRequest")
public record CreateSongRequest(
        @Schema(description = "Artist ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID artistId,

        @Schema(example = "Under the Bridge")
        String title,

        @Schema(example = "E")
        String musicalKey,

        @Schema(example = "84")
        Integer bpm,

        @Schema(example = "Practice intro arpeggio slowly; focus on clean transitions.")
        String notes
) { }