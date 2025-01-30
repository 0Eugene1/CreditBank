FROM openjdk:21-jdk-slim
WORKDIR /deal
COPY ./target/deal.jar /app/
EXPOSE 8082
CMD ["java", "-jar", "deal.jar"]