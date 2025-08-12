# Multi-stage build for Patient Service
FROM maven:3.9.4-openjdk-17-slim AS build

# Set working directory
WORKDIR /app

# Copy parent pom.xml first for dependency resolution
COPY pom.xml ./
COPY patient-service/pom.xml ./patient-service/

# Download dependencies (better caching)
RUN mvn dependency:go-offline -B -pl patient-service

# Copy source code for patient-service only
COPY patient-service/src ./patient-service/src

# Build only the patient service
RUN mvn clean package -pl patient-service -am -DskipTests

# Runtime stage
FROM openjdk:17-jre-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN groupadd -r patientuser && useradd -r -g patientuser patientuser

# Set working directory
WORKDIR /app

# Create directory for H2 database files (if persistence is needed)
RUN mkdir -p /app/data && chown -R patientuser:patientuser /app/data

# Copy the built JAR file from build stage
COPY --from=build /app/patient-service/target/*.jar patient-service.jar

# Change ownership to non-root user
RUN chown -R patientuser:patientuser /app
USER patientuser

# Expose the port for patient service
EXPOSE 8081

# Health check - updated to use the correct context path
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8081/patient-service/actuator/health || exit 1

# Environment variables
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE=default

# H2 Database configuration
ENV H2_DATABASE_PATH="/app/data/patientdb"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/patient-service.jar"]