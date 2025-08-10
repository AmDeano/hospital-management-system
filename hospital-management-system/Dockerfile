# Root Dockerfile for Hospital Management System
# This builds all microservices in a multi-stage build

# Build stage
FROM maven:3.9.4-openjdk-17-slim AS build

# Set working directory
WORKDIR /app

# Copy parent POM first (for better layer caching)
COPY pom.xml .

# Copy all module POMs
COPY patient-service/pom.xml ./patient-service/
COPY employee_service/pom.xml ./employee_service/

# Download dependencies (this layer will be cached if POMs don't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY patient-service/src ./patient-service/src
COPY employee_service/src ./employee_service/src

# Build all modules
RUN mvn clean package -DskipTests

# Runtime stage for patient-service
FROM openjdk:17-jdk-slim AS patient-service

WORKDIR /app

# Copy the built JAR
COPY --from=build /app/patient-service/target/*.jar app.jar

# Create non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Runtime stage for employee-service
FROM openjdk:17-jdk-slim AS employee-service

WORKDIR /app

# Copy the built JAR
COPY --from=build /app/employee_service/target/*.jar app.jar

# Create non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Expose port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]