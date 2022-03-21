FROM openjdk:11

WORKDIR ./home

COPY ./backend/target/backend-1.3.jar .
COPY ./docker/application.properties .

RUN mkdir -p ./deployment ./tmp ./logs

CMD ["java", "-jar", "./backend-1.3.jar", "--spring.config.location=file:///home/application.properties"]

EXPOSE 8990
