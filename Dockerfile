FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=target/cowrite-0.0.1-SNAPSHOT.jar
ENV SPRING_APPLICATION_NAME=cowrite

WORKDIR /app
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]