# Use an official Maven image to build the app
FROM maven:3.8.4-openjdk-11-slim AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies (this will be cached)
COPY pom.xml .

RUN mvn dependency:go-offline

# Copy the source code
COPY src /app/src

# Build the application using Maven
RUN mvn clean package -DskipTests

# Use a minimal base image to run the app
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/vertxmart-1.0-SNAPSHOT.jar /app/vertxmart.jar

# Expose the port that your Vert.x service will listen on
EXPOSE 8081

# Run the Vert.x application
CMD ["java", "-jar", "/app/vertxmart.jar"]
