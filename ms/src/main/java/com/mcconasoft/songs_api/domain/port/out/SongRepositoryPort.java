package com.mcconasoft.songs_api.domain.port.out;

import com.mcconasoft.songs_api.domain.model.Song;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SongRepositoryPort {
    Song save(Song song);
    Optional<Song> findById(UUID id);
    List<Song> findAll();
    List<Song> findAllByArtistId(UUID artistId);
    void deleteById(UUID id);
}