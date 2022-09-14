FROM openjdk:18-alpine
RUN addgroup -S paymentchain && adduser -S admin -G paymentchain
USER admin:paymentchain
VOLUME /tmp
ARG JAR_FILE=target/*.jar
ADD target/${JAR_FILE} app.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]


#FROM maven:3-jdk-8-alpine as paso1
#COPY pom.xml .
#RUN mvn -X -e -B dependency:resolve
#COPY src ./src
#RUN mvn -e -B package
#
#FROM openjdk:18-alpine
#COPY --from=paso1 target/eurekaserver-1.0-SNAPSHOT.jar ./app.jar
#CMD ["java", "-jar", "app.jar"]