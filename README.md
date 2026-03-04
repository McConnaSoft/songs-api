# songs-api

Spring Boot API for artists and songs, backed by PostgreSQL and managed with Liquibase migrations.

## Stack

- Java 21
- Spring Boot 4
- Spring Data JPA
- Liquibase
- PostgreSQL
- JUnit 5 + Testcontainers (integration tests)

## Repository layout

- `ms/` - Spring Boot service
- `k8s/` - Kubernetes manifests
- `scripts/` - deployment and utility scripts

## Local prerequisites

- Java 21
- Docker Desktop (recommended for local Postgres and integration tests)
- PowerShell

## Run locally

### 1. Start PostgreSQL

From repo root:

```powershell
docker run --name songs-postgres `
  -e POSTGRES_DB=songs `
  -e POSTGRES_USER=songs `
  -e POSTGRES_PASSWORD=changeme `
  -p 5432:5432 `
  -d postgres:16-alpine
```

The app default config points to:

- `jdbc:postgresql://localhost:5432/songs`
- user `songs`
- password from `SONGS_DB_PASSWORD` (default `changeme`)

### 2. Start the API

```powershell
Set-Location .\ms
$env:SONGS_DB_PASSWORD="changeme"
.\mvnw.cmd spring-boot:run
```

Liquibase runs automatically at startup and creates/updates schema from:

- `ms/src/main/resources/db/changelog/db.changelog-master.yaml`

API base URL:

- `http://localhost:8080`

Swagger UI:

- `http://localhost:8080/swagger-ui/index.html`

## Quick API smoke calls

Create artist:

```powershell
curl -X POST http://localhost:8080/artists `
  -H "Content-Type: application/json" `
  -d '{"name":"Nirvana"}'
```

Create song:

```powershell
curl -X POST http://localhost:8080/songs `
  -H "Content-Type: application/json" `
  -d '{"artistId":"<artist-uuid>","title":"Come As You Are","musicalKey":"E","bpm":120}'
```

List songs by artist:

```powershell
curl "http://localhost:8080/songs?artistId=<artist-uuid>"
```

## Validation behavior

The API now validates request bodies and returns `400` with structured errors:

```json
{
  "error": "validation_error",
  "message": "Validation failed",
  "fields": {
    "name": "name is required"
  }
}
```

## Testing

From `ms/`:

Run all tests:

```powershell
.\mvnw.cmd test
```

Run only integration tests:

```powershell
.\mvnw.cmd -Dtest=ApiIntegrationTests test
```

Notes:

- Integration tests use Testcontainers + PostgreSQL.
- If Docker is unavailable, integration tests are auto-skipped.
- Unit/smoke tests still run without Docker.

## Stop local Postgres

```powershell
docker stop songs-postgres
docker rm songs-postgres
```
