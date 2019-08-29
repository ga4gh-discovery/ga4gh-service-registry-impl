FROM gradle:jdk11 as builder

ARG APP_NAME
ARG APP_VERSION

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src/target

RUN ./gradlew -Pversion=${APP_VERSION} clean build

FROM openjdk:11-jdk-slim

COPY --from=builder /home/gradle/src/target/build/**/*.jar /app.jar

EXPOSE 8080

ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar