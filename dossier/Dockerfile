FROM openjdk:21-jdk-slim
WORKDIR /dossier
COPY ./target/dossier.jar /app/
EXPOSE 8085
CMD ["java", "-jar", "dossier.jar"]