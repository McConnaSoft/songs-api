package com.mcconasoft.songs_api.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtistJpaRepository extends JpaRepository<ArtistJpaEntity, UUID> {
}