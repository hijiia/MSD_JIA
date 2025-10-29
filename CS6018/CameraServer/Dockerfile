# Multi-stage build for FancyCameraServer

# Stage 1: Build the application
FROM gradle:8.9-jdk21 AS build

WORKDIR /app

# Copy gradle files first for caching
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY src ./src

# Build the application
RUN gradle buildFatJar --no-daemon

# Stage 2: Create runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*-all.jar app.jar

# Create uploads directory for photos
RUN mkdir -p /app/uploads

# Expose the application port
EXPOSE 8080

# Set environment variables (can be overridden by docker-compose)
ENV LDAP_URL="ldap://ldap:389"

# Run the application
CMD ["java", "-jar", "app.jar"]
