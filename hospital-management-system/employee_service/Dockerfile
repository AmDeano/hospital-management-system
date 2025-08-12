# Multi-stage build for Employee Service
FROM maven:3.9.4-openjdk-17-slim AS build

# Set working directory
WORKDIR /app

# Copy parent pom.xml first for dependency resolution
COPY pom.xml ./
COPY employee_service/pom.xml ./employee_service/

# Download dependencies (better caching)
RUN mvn dependency:go-offline -B -pl employee_service

# Copy source code for employee_service only
COPY employee_service/src ./employee_service/src

# Build only the employee service
RUN mvn clean package -pl employee_service -am -DskipTests

# Runtime stage
FROM openjdk:17-jre-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN groupadd -r employeeuser && useradd -r -g employeeuser employeeuser

# Set working directory
WORKDIR /app

# Create directory for H2 database files (if persistence is needed)
RUN mkdir -p /app/data && chown -R employeeuser:employeeuser /app/data

# Copy the built JAR file from build stage
COPY --from=build /app/employee_service/target/*.jar employee-service.jar

# Change ownership to non-root user
RUN chown -R employeeuser:employeeuser /app
USER employeeuser

# Expose the port for employee service
EXPOSE 8082

# Health check - updated to use the correct context path
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8082/employee-service/actuator/health || exit 1

# Environment variables
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE=default

# H2 Database configuration
ENV H2_DATABASE_PATH="/app/data/employeedb"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/employee-service.jar"]