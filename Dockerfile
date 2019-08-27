FROM openjdk:11-jdk-slim
MAINTAINER Milan Panik <milan@dnastack.com>

ADD build/**/*.jar /app.jar

EXPOSE 8080

ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar