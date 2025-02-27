# Stage 1: Build stage
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM openjdk:17.0.1-jdk-slim

# Install necessary tools and libraries for C++ and Python
RUN apt-get update && \
    apt-get install -y python3 && \
    rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /target/PrepWell-0.0.1-SNAPSHOT.jar PrepWell.jar

# Expose the port
EXPOSE 8080

# Entrypoint command to run the Java application
ENTRYPOINT ["java", "-jar", "PrepWell.jar"]
