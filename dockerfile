# Stage 1: Build the application using Maven
FROM maven:3.8.5-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install

# Stage 2: Create a slim, runnable image
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/healthcare-system-1.0-SNAPSHOT.jar ./app.jar
EXPOSE 4567
CMD ["java", "-jar", "app.jar"]
