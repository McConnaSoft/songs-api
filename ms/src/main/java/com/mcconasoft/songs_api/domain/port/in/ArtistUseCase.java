package com.mcconasoft.songs_api.domain.port.in;

import com.mcconasoft.songs_api.domain.model.Artist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistUseCase {
    Artist create(Artist artist);
    List<Artist> list();
    Optional<Artist> get(UUID id);
    Artist rename(UUID id, String name);
    void delete(UUID id);
}