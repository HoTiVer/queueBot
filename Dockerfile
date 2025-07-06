FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

RUN apt-get update && apt-get install -y maven

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk

WORKDIR /app

EXPOSE 8080

COPY --from=build /app/target/bot-app.jar app.jar

CMD ["java", "-jar", "app.jar"]