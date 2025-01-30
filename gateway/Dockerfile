FROM openjdk:21-jdk-slim
WORKDIR /gateway
COPY ./target/gateway.jar /app/
EXPOSE 8084
CMD ["java", "-jar", "gateway.jar"]