FROM openjdk:17-alpine

ENV DS_SERVER_PORT=8980

WORKDIR /app

ARG JAR_FILE_VERSION=0.0.1-SNAPSHOT
ARG JAR_FILE=target/data-store-server-${JAR_FILE_VERSION}.jar

COPY ${JAR_FILE} data-store-server-app.jar

EXPOSE ${DS_SERVER_PORT}

ENTRYPOINT ["java", "-jar", "data-store-server-app.jar", \
            "--server.port=${DS_SERVER_PORT}", \
            "--spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT:5432}/${POSTGRES_DB}?currentSchema=ds_server&stringtype=unspecified", \
            "--spring.datasource.username=${POSTGRES_USER}", \
            "--spring.datasource.password=${POSTGRES_PASSWORD}"]