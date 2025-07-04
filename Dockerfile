FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/QueueBot-0.0.5.jar app.jar

CMD ["java", "-jar", "app.jar"]