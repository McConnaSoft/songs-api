package com.mcconasoft.songs_api.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "ArtistResponse")
public record ArtistResponse(
        UUID id,
        String name
) { }