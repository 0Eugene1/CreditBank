FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar calculator.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "calculator.jar"]
