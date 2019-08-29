# This is a two stage build, as described in the Docker docs: https://docs.docker.com/develop/develop-images/multistage-build/

##
## Stage 1: Build and test the Java Sources
##

# This stage depends on a JDK image and uses Maven Wrapper to pull in dependencies and build the project
# from source.
#FROM openjdk:11-jdk-slim
FROM openjdk:8-jdk-alpine
ARG APP_NAME
ARG APP_VERSION

ADD target /
ADD target/build.gradle ./
ADD target/gradlew ./
ADD target/src ./src

RUN ./gradlew -Pversion=${app_version} clean build




## Before bringing in project sources, resolve maven dependencies.
## This allows Docker to reuse the cached image with all dependencies resolved.
#ADD target/build.gradle ./
#ADD target/.gradle ./.gradle
#ADD target/.gradlew ./
##RUN ./mvnw -B de.qaware.maven:go-offline-maven-plugin:resolve-dependencies
#
#
## Now add sources, which will bust the cache.
#ADD target/src ./src
##RUN ./mvnw -B -o package -DskipTests=true
#RUN ./gradlew -Pversion=${APP_VERSION} clean build
#
###
### Stage 2: Package the runnable test image
###
#
## This stage uses a JRE rather than JDK because it doesn't have to compile any Java sources.
## It is assembling the runnable test container and packaging all its Maven dependencies
#FROM openjdk:11-slim
#ARG APP_NAME
#ARG APP_VERSION
#
#ADD target/build.gradle /build.gradle
#ADD target/.gradle /.gradle
#ADD target/gradlew /
#COPY --from=0 /build/target/${APP_NAME}*.jar /e2e-tests.jar
##RUN ./mvnw -B de.qaware.maven:go-offline-maven-plugin:resolve-dependencies
#
##ENTRYPOINT ["./mvnw", "-o", "test"]
ENTRYPOINT ["./gradlew", "endToEndTest", "--info"]
