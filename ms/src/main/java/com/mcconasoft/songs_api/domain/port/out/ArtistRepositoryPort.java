package com.mcconasoft.songs_api.domain.port.out;

import com.mcconasoft.songs_api.domain.model.Artist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistRepositoryPort {
    Artist save(Artist artist);
    Optional<Artist> findById(UUID id);
    List<Artist> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}