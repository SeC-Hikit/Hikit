FROM openjdk:8u201-jdk-alpine3.9
COPY ./backend/target/backend-1.0-SNAPSHOT.jar .
COPY ./backend/src/main/resources/application.properties .
CMD ["java", "-jar", "backend-1.0-SNAPSHOT.jar", "--spring.config.location", "file:/"]

EXPOSE 8990