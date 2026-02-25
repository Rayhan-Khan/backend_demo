# First stage: Build the application
FROM gradle:8.2-jdk17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy Gradle Wrapper and configuration files
COPY gradlew gradlew.bat /app/
COPY gradle /app/gradle/
COPY build.gradle settings.gradle /app/

# Ensure Gradle Wrapper is executable
RUN chmod +x /app/gradlew

# Download dependencies separately to leverage Docker caching
RUN ./gradlew dependencies --no-daemon

# Copy the application source code
COPY src /app/src

# Build the application
RUN ./gradlew bootJar --no-daemon

# Second stage: Prepare the final runtime image
FROM eclipse-temurin:17-jre-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
