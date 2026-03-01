package com.mcconasoft.songs_api.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SongJpaRepository extends JpaRepository<SongJpaEntity, UUID> {
    List<SongJpaEntity> findAllByArtist_Id(UUID artistId);
}