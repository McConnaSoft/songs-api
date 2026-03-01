package com.mcconasoft.songs_api.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CreateArtistRequest")
public record CreateArtistRequest(
        @Schema(example = "Red Hot Chili Peppers")
        String name
) { }