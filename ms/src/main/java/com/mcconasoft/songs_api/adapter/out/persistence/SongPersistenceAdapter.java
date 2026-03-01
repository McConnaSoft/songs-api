package com.mcconasoft.songs_api.adapter.out.persistence;

import com.mcconasoft.songs_api.adapter.out.persistence.mapper.SongEntityMapper;
import com.mcconasoft.songs_api.domain.model.Song;
import com.mcconasoft.songs_api.domain.port.out.SongRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SongPersistenceAdapter implements SongRepositoryPort {

    private final SongJpaRepository songs;
    private final ArtistJpaRepository artists;

    public SongPersistenceAdapter(SongJpaRepository songs, ArtistJpaRepository artists) {
        this.songs = songs;
        this.artists = artists;
    }

    @Override
    public Song save(Song song) {
        ArtistJpaEntity artistRef = artists.getReferenceById(song.artistId());
        return SongEntityMapper.toDomain(songs.save(SongEntityMapper.toEntity(song, artistRef)));
    }

    @Override
    public Optional<Song> findById(UUID id) {
        return songs.findById(id).map(SongEntityMapper::toDomain);
    }

    @Override
    public List<Song> findAll() {
        return songs.findAll().stream().map(SongEntityMapper::toDomain).toList();
    }

    @Override
    public List<Song> findAllByArtistId(UUID artistId) {
        return songs.findAllByArtist_Id(artistId).stream().map(SongEntityMapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        songs.deleteById(id);
    }
}