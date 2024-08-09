# Примечание:
# контекст Dockerfile определён относительно корневого каталога всего проета
#
# Сборка относительно корневого каталога:
# docker buildx build -f data-store-server/Dockerfile -t msvdevk/data-store-server:latest .
#
FROM maven:3.9.8-eclipse-temurin-17-alpine as builder

WORKDIR /app/openapi
ADD openapi/openapi.yaml .

WORKDIR /app/source
COPY data-store-server/pom.xml .
COPY data-store-server/src/main/ ./src/main/

RUN mvn --errors --strict-checksums --threads 1C verify


FROM openjdk:17-alpine

WORKDIR /app
COPY --from=builder /app/source/target/data-store-server-*.jar data-store-server-app.jar

ENV DS_SERVER_PORT=8980
EXPOSE ${DS_SERVER_PORT}

ENTRYPOINT ["java", "-jar", "data-store-server-app.jar", \
            "--server.port=${DS_SERVER_PORT}", \
            "--spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT:5432}/${POSTGRES_DB}?currentSchema=ds_server&stringtype=unspecified", \
            "--spring.datasource.username=${POSTGRES_USER}", \
            "--spring.datasource.password=${POSTGRES_PASSWORD}"]