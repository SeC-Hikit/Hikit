FROM openjdk:8u201-jdk-alpine3.9

WORKDIR ./home

COPY ./backend/target/backend-1.0.jar .
COPY ./docker/application.properties .

RUN mkdir -p ./deployment ./tmp ./logs

CMD ["java", "-jar", "./backend-1.0.jar", "--spring.config.location=file:///home/application.properties"]

EXPOSE 8990
