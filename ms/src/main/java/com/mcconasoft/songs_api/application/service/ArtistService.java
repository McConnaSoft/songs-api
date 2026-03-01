package com.mcconasoft.songs_api.application.service;

import com.mcconasoft.songs_api.domain.model.Artist;
import com.mcconasoft.songs_api.domain.port.in.ArtistUseCase;
import com.mcconasoft.songs_api.domain.port.out.ArtistRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ArtistService implements ArtistUseCase {

    private final ArtistRepositoryPort artists;

    public ArtistService(ArtistRepositoryPort artists) {
        this.artists = artists;
    }

    @Override
    public Artist create(Artist artist) {
        UUID id = (artist.id() != null) ? artist.id() : UUID.randomUUID();
        return artists.save(new Artist(id, artist.name()));
    }

    @Override
    public List<Artist> list() {
        return artists.findAll();
    }

    @Override
    public Optional<Artist> get(UUID id) {
        return artists.findById(id);
    }

    @Override
    public Artist rename(UUID id, String name) {
        Artist existing = artists.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artist not found: " + id));
        return artists.save(new Artist(existing.id(), name));
    }

    @Override
    public void delete(UUID id) {
        artists.deleteById(id);
    }
}