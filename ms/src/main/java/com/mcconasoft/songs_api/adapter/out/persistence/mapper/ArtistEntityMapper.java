package com.mcconasoft.songs_api.adapter.out.persistence.mapper;

import com.mcconasoft.songs_api.adapter.out.persistence.ArtistJpaEntity;
import com.mcconasoft.songs_api.domain.model.Artist;

public final class ArtistEntityMapper {
    private ArtistEntityMapper() {}

    public static ArtistJpaEntity toEntity(Artist a) {
        ArtistJpaEntity e = new ArtistJpaEntity();
        e.setId(a.id());
        e.setName(a.name());
        return e;
    }

    public static Artist toDomain(ArtistJpaEntity e) {
        return new Artist(e.getId(), e.getName());
    }
}