package com.mcconasoft.songs_api.domain.model;

import java.util.UUID;

public record Song(
        UUID id,
        UUID artistId,
        String title,
        String musicalKey,
        Integer bpm,
        String notes
) { }