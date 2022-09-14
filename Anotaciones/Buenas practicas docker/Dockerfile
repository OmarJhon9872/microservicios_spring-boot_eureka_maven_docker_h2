FROM maven:3-jdk-8-alpine as paso1
COPY pom.xml .
RUN mvn -X -e -B dependency:resolve
COPY src ./src
RUN mvn -e -B package

FROM openjdk:18-alpine
COPY --from=paso1 target/eurekaserver-1.0-SNAPSHOT.jar ./app.jar
CMD ["java", "-jar", "app.jar"]