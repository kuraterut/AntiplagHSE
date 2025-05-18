FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/FileStorageService-1.0-SNAPSHOT.jar app.jar
RUN mkdir -p /app/uploads
ENTRYPOINT ["java", "-jar", "app.jar"]