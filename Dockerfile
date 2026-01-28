# syntax=docker/dockerfile:1
FROM maven:3.9.7-eclipse-temurin-17 AS build
WORKDIR /app

# Copy build files first for better layer caching
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn ./.mvn

# Download dependencies
RUN ./mvnw -q -DskipTests dependency:go-offline

# Copy source and build
COPY src ./src
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
