FROM openjdk:17-jdk-slim
WORKDIR /calculator
COPY ./target/calculator.jar /app/
EXPOSE 8079
CMD ["java", "-jar", "calculator.jar"]
