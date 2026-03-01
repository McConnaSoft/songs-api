package com.mcconasoft.songs_api.domain.model;

import java.util.UUID;

public record Artist(
        UUID id,
        String name
) { }