package com.mcconasoft.songs_api.config;

import com.mcconasoft.songs_api.adapter.out.persistence.*;
import com.mcconasoft.songs_api.application.service.ArtistService;
import com.mcconasoft.songs_api.application.service.SongService;
import com.mcconasoft.songs_api.domain.port.in.ArtistUseCase;
import com.mcconasoft.songs_api.domain.port.in.SongUseCase;
import com.mcconasoft.songs_api.domain.port.out.ArtistRepositoryPort;
import com.mcconasoft.songs_api.domain.port.out.SongRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ArtistRepositoryPort artistRepositoryPort(ArtistJpaRepository jpaRepo) {
        return new ArtistPersistenceAdapter(jpaRepo);
    }

    @Bean
    public SongRepositoryPort songRepositoryPort(SongJpaRepository songRepo, ArtistJpaRepository artistRepo) {
        return new SongPersistenceAdapter(songRepo, artistRepo);
    }

    @Bean
    public ArtistUseCase artistUseCase(ArtistRepositoryPort artists) {
        return new ArtistService(artists);
    }

    @Bean
    public SongUseCase songUseCase(SongRepositoryPort songs, ArtistRepositoryPort artists) {
        return new SongService(songs, artists);
    }
}