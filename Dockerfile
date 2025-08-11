# Use OpenJDK 21 as base image
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /app

# Copy the JAR file
COPY build/libs/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application with optimized JVM settings for cloud environments
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
