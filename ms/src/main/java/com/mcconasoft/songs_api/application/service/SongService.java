package com.mcconasoft.songs_api.application.service;

import com.mcconasoft.songs_api.domain.model.Song;
import com.mcconasoft.songs_api.domain.port.in.SongUseCase;
import com.mcconasoft.songs_api.domain.port.out.ArtistRepositoryPort;
import com.mcconasoft.songs_api.domain.port.out.SongRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SongService implements SongUseCase {

    private final SongRepositoryPort songs;
    private final ArtistRepositoryPort artists;

    public SongService(SongRepositoryPort songs, ArtistRepositoryPort artists) {
        this.songs = songs;
        this.artists = artists;
    }

    @Override
    public Song create(Song song) {
        if (song.artistId() == null) {
            throw new IllegalArgumentException("artistId is required");
        }
        if (!artists.existsById(song.artistId())) {
            throw new IllegalArgumentException("Artist does not exist: " + song.artistId());
        }
        UUID id = (song.id() != null) ? song.id() : UUID.randomUUID();
        return songs.save(new Song(id, song.artistId(), song.title(), song.musicalKey(), song.bpm(), song.notes()));
    }

    @Override
    public List<Song> list(UUID artistIdOrNull) {
        return (artistIdOrNull == null) ? songs.findAll() : songs.findAllByArtistId(artistIdOrNull);
    }

    @Override
    public Optional<Song> get(UUID id) {
        return songs.findById(id);
    }

    @Override
    public Song update(UUID id, Song song) {
        Song existing = songs.findById(id).orElseThrow(() -> new IllegalArgumentException("Song not found: " + id));

        UUID artistId = (song.artistId() != null) ? song.artistId() : existing.artistId();
        if (!artists.existsById(artistId)) {
            throw new IllegalArgumentException("Artist does not exist: " + artistId);
        }

        String title = (song.title() != null) ? song.title() : existing.title();
        String key = (song.musicalKey() != null) ? song.musicalKey() : existing.musicalKey();
        Integer bpm = (song.bpm() != null) ? song.bpm() : existing.bpm();
        String notes = (song.notes() != null) ? song.notes() : existing.notes();

        return songs.save(new Song(existing.id(), artistId, title, key, bpm, notes));
    }

    @Override
    public void delete(UUID id) {
        songs.deleteById(id);
    }
}