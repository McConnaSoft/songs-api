package com.mcconasoft.songs_api.adapter.out.persistence;

import com.mcconasoft.songs_api.adapter.out.persistence.mapper.ArtistEntityMapper;
import com.mcconasoft.songs_api.domain.model.Artist;
import com.mcconasoft.songs_api.domain.port.out.ArtistRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ArtistPersistenceAdapter implements ArtistRepositoryPort {

    private final ArtistJpaRepository repo;

    public ArtistPersistenceAdapter(ArtistJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Artist save(Artist artist) {
        return ArtistEntityMapper.toDomain(repo.save(ArtistEntityMapper.toEntity(artist)));
    }

    @Override
    public Optional<Artist> findById(UUID id) {
        return repo.findById(id).map(ArtistEntityMapper::toDomain);
    }

    @Override
    public List<Artist> findAll() {
        return repo.findAll().stream().map(ArtistEntityMapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repo.existsById(id);
    }
}