package com.mcconasoft.songs_api.adapter.in.web;

import com.mcconasoft.songs_api.adapter.in.web.dto.CreateSongRequest;
import com.mcconasoft.songs_api.adapter.in.web.dto.SongResponse;
import com.mcconasoft.songs_api.domain.model.Song;
import com.mcconasoft.songs_api.domain.port.in.SongUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Songs")
@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongUseCase songs;

    public SongController(SongUseCase songs) {
        this.songs = songs;
    }

    @Operation(summary = "Create a song")
    @PostMapping
    public ResponseEntity<SongResponse> create(@Valid @RequestBody CreateSongRequest req) {
        Song created = songs.create(new Song(null, req.artistId(), req.title(), req.musicalKey(), req.bpm(), req.notes()));
        return ResponseEntity.created(URI.create("/songs/" + created.id()))
                .body(new SongResponse(created.id(), created.artistId(), created.title(), created.musicalKey(), created.bpm(), created.notes()));
    }

    @Operation(summary = "List songs (optionally filter by artistId)")
    @GetMapping
    public List<SongResponse> list(@RequestParam(required = false) UUID artistId) {
        return songs.list(artistId).stream()
                .map(s -> new SongResponse(s.id(), s.artistId(), s.title(), s.musicalKey(), s.bpm(), s.notes()))
                .toList();
    }

    @Operation(summary = "Get song by id")
    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> get(@PathVariable UUID id) {
        return songs.get(id)
                .map(s -> ResponseEntity.ok(new SongResponse(s.id(), s.artistId(), s.title(), s.musicalKey(), s.bpm(), s.notes())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update a song")
    @PutMapping("/{id}")
    public ResponseEntity<SongResponse> update(@PathVariable UUID id, @Valid @RequestBody CreateSongRequest req) {
        Song updated = songs.update(id, new Song(null, req.artistId(), req.title(), req.musicalKey(), req.bpm(), req.notes()));
        return ResponseEntity.ok(new SongResponse(updated.id(), updated.artistId(), updated.title(), updated.musicalKey(), updated.bpm(), updated.notes()));
    }

    @Operation(summary = "Delete a song")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        songs.delete(id);
        return ResponseEntity.noContent().build();
    }
}
