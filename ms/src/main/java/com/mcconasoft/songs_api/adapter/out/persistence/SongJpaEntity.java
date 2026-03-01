package com.mcconasoft.songs_api.adapter.out.persistence;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
        name = "song",
        uniqueConstraints = @UniqueConstraint(name = "uq_song_artist_title", columnNames = {"artist_id", "title"})
)
public class SongJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistJpaEntity artist;

    @Column(nullable = false)
    private String title;

    @Column(name = "musical_key")
    private String musicalKey;

    private Integer bpm;

    @Column(columnDefinition = "text")
    private String notes;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public ArtistJpaEntity getArtist() { return artist; }
    public void setArtist(ArtistJpaEntity artist) { this.artist = artist; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMusicalKey() { return musicalKey; }
    public void setMusicalKey(String musicalKey) { this.musicalKey = musicalKey; }

    public Integer getBpm() { return bpm; }
    public void setBpm(Integer bpm) { this.bpm = bpm; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}