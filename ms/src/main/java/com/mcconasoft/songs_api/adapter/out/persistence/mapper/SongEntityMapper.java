package com.mcconasoft.songs_api.adapter.out.persistence.mapper;

import com.mcconasoft.songs_api.adapter.out.persistence.ArtistJpaEntity;
import com.mcconasoft.songs_api.adapter.out.persistence.SongJpaEntity;
import com.mcconasoft.songs_api.domain.model.Song;

public final class SongEntityMapper {
    private SongEntityMapper() {}

    public static SongJpaEntity toEntity(Song s, ArtistJpaEntity artistRef) {
        SongJpaEntity e = new SongJpaEntity();
        e.setId(s.id());
        e.setArtist(artistRef);
        e.setTitle(s.title());
        e.setMusicalKey(s.musicalKey());
        e.setBpm(s.bpm());
        e.setNotes(s.notes());
        return e;
    }

    public static Song toDomain(SongJpaEntity e) {
        return new Song(
                e.getId(),
                e.getArtist().getId(),
                e.getTitle(),
                e.getMusicalKey(),
                e.getBpm(),
                e.getNotes()
        );
    }
}