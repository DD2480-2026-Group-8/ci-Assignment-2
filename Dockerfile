# Multi-stage build for Java CI Server
# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /build

# Copy the entire ci-app directory
COPY ci-app/ ./

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy the shaded (fat) JAR from build stage
# The shade plugin creates a -shaded.jar with all dependencies and the Main-Class set.
COPY --from=build /build/target/*-shaded.jar app.jar

# Expose port 8080 (internal container port)
EXPOSE 8080

# Set JVM options for memory management
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
