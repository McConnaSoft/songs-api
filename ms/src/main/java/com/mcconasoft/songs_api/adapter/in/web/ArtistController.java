package com.mcconasoft.songs_api.adapter.in.web;

import com.mcconasoft.songs_api.adapter.in.web.dto.ArtistResponse;
import com.mcconasoft.songs_api.adapter.in.web.dto.CreateArtistRequest;
import com.mcconasoft.songs_api.domain.model.Artist;
import com.mcconasoft.songs_api.domain.port.in.ArtistUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Artists")
@RestController
@RequestMapping("/artists")
public class ArtistController {

    private final ArtistUseCase artists;

    public ArtistController(ArtistUseCase artists) {
        this.artists = artists;
    }

    @Operation(summary = "Create an artist")
    @PostMapping
    public ResponseEntity<ArtistResponse> create(@Valid @RequestBody CreateArtistRequest req) {
        Artist created = artists.create(new Artist(null, req.name()));
        return ResponseEntity.created(URI.create("/artists/" + created.id()))
                .body(new ArtistResponse(created.id(), created.name()));
    }

    @Operation(summary = "List artists")
    @GetMapping
    public List<ArtistResponse> list() {
        return artists.list().stream()
                .map(a -> new ArtistResponse(a.id(), a.name()))
                .toList();
    }

    @Operation(summary = "Get artist by id")
    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponse> get(@PathVariable UUID id) {
        return artists.get(id)
                .map(a -> ResponseEntity.ok(new ArtistResponse(a.id(), a.name())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Rename an artist")
    @PutMapping("/{id}")
    public ResponseEntity<ArtistResponse> rename(@PathVariable UUID id, @Valid @RequestBody CreateArtistRequest req) {
        Artist updated = artists.rename(id, req.name());
        return ResponseEntity.ok(new ArtistResponse(updated.id(), updated.name()));
    }

    @Operation(summary = "Delete an artist (fails if songs exist)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        artists.delete(id);
        return ResponseEntity.noContent().build();
    }
}
