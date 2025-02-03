FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/deal-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
