package com.mcconasoft.songs_api.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "SongResponse")
public record SongResponse(
        UUID id,
        UUID artistId,
        String title,
        String musicalKey,
        Integer bpm,
        String notes
) { }