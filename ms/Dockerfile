# syntax=docker/dockerfile:1.6

# -------- Stage 1: Build (multi-arch friendly) --------
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Install Maven (works on arm64 + arm/v7 because this is Debian-based)
RUN apt-get update \
 && apt-get install -y --no-install-recommends maven \
 && rm -rf /var/lib/apt/lists/*

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# -------- Stage 2: Runtime --------
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]