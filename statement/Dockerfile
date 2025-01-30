FROM openjdk:21-jdk-slim
WORKDIR /statement
COPY ./target/statement.jar /app/
EXPOSE 8083
CMD ["java", "-jar", "statement.jar"]