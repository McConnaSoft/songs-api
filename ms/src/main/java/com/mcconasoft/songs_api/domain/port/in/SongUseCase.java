package com.mcconasoft.songs_api.domain.port.in;

import com.mcconasoft.songs_api.domain.model.Song;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SongUseCase {
    Song create(Song song);
    List<Song> list(UUID artistIdOrNull);
    Optional<Song> get(UUID id);
    Song update(UUID id, Song song);
    void delete(UUID id);
}