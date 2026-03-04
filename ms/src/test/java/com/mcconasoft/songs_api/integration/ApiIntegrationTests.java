package com.mcconasoft.songs_api.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class ApiIntegrationTests {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("songs")
            .withUsername("songs")
            .withPassword("songs");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE song, artist");
    }

    @Test
    void createArtistAndSongThenFilterByArtist() throws Exception {
        HttpResponse<String> artistResponse = postJson("/artists", """
                {"name":"Nirvana"}
                """);
        assertThat(artistResponse.statusCode()).isEqualTo(201);

        JsonNode artist = objectMapper.readTree(artistResponse.body());
        String artistId = artist.get("id").asText();
        assertThat(artist.get("name").asText()).isEqualTo("Nirvana");

        HttpResponse<String> songResponse = postJson("/songs", """
                {
                  "artistId":"%s",
                  "title":"Come As You Are",
                  "musicalKey":"E",
                  "bpm":120,
                  "notes":"Palm mute intro"
                }
                """.formatted(artistId));
        assertThat(songResponse.statusCode()).isEqualTo(201);

        JsonNode song = objectMapper.readTree(songResponse.body());
        String songId = song.get("id").asText();
        assertThat(song.get("artistId").asText()).isEqualTo(artistId);
        assertThat(song.get("title").asText()).isEqualTo("Come As You Are");

        HttpResponse<String> filteredSongsResponse = get("/songs?artistId=" + artistId);
        assertThat(filteredSongsResponse.statusCode()).isEqualTo(200);
        JsonNode filteredSongs = objectMapper.readTree(filteredSongsResponse.body());
        assertThat(filteredSongs.isArray()).isTrue();
        assertThat(filteredSongs).hasSize(1);
        assertThat(filteredSongs.get(0).get("id").asText()).isEqualTo(songId);
        assertThat(filteredSongs.get(0).get("artistId").asText()).isEqualTo(artistId);
        assertThat(filteredSongs.get(0).get("title").asText()).isEqualTo("Come As You Are");

        HttpResponse<String> songByIdResponse = get("/songs/" + songId);
        assertThat(songByIdResponse.statusCode()).isEqualTo(200);
        JsonNode songById = objectMapper.readTree(songByIdResponse.body());
        assertThat(songById.get("id").asText()).isEqualTo(songId);
    }

    @Test
    void createSongWithMissingArtistReturnsBadRequest() throws Exception {
        String missingArtistId = UUID.randomUUID().toString();
        HttpResponse<String> songResponse = postJson("/songs", """
                {
                  "artistId":"%s",
                  "title":"No Artist Song",
                  "musicalKey":"A",
                  "bpm":100
                }
                """.formatted(missingArtistId));
        assertThat(songResponse.statusCode()).isEqualTo(400);

        JsonNode errorBody = objectMapper.readTree(songResponse.body());
        assertThat(errorBody.get("error").asText()).isEqualTo("bad_request");
        assertThat(errorBody.get("message").asText()).isEqualTo("Artist does not exist: " + missingArtistId);
    }

    @Test
    void createArtistWithBlankNameReturnsValidationError() throws Exception {
        HttpResponse<String> artistResponse = postJson("/artists", """
                {"name":" "}
                """);
        assertThat(artistResponse.statusCode()).isEqualTo(400);

        JsonNode errorBody = objectMapper.readTree(artistResponse.body());
        assertThat(errorBody.get("error").asText()).isEqualTo("validation_error");
        assertThat(errorBody.get("message").asText()).isEqualTo("Validation failed");
        assertThat(errorBody.get("fields").get("name").asText()).isEqualTo("name is required");
    }

    @Test
    void deletingArtistWithSongsReturnsServerErrorDueToFkRestriction() throws Exception {
        HttpResponse<String> artistResponse = postJson("/artists", """
                {"name":"The Police"}
                """);
        assertThat(artistResponse.statusCode()).isEqualTo(201);

        String artistId = objectMapper.readTree(artistResponse.body()).get("id").asText();

        HttpResponse<String> songResponse = postJson("/songs", """
                {
                  "artistId":"%s",
                  "title":"Message in a Bottle"
                }
                """.formatted(artistId));
        assertThat(songResponse.statusCode()).isEqualTo(201);

        HttpResponse<String> deleteResponse = delete("/artists/" + artistId);
        assertThat(deleteResponse.statusCode()).isGreaterThanOrEqualTo(500);
    }

    private HttpResponse<String> postJson(String path, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri(path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri(path))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> delete(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri(path))
                .DELETE()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }
}
