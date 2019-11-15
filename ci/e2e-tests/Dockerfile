FROM openjdk:11-jdk-slim
ARG APP_NAME
ARG APP_VERSION

# Before bringing in project sources, resolve dependencies.
# This allows Docker to reuse the cached image with all dependencies resolved.
ADD target/build.gradle .
ADD target/settings.gradle .
ADD target/gradle gradle
ADD target/gradlew .
RUN find .
RUN ./gradlew resolveAllDependencies

# Now add sources, which will bust the cache.
ADD target/src ./src

# build the tests so they are already compiled when this image runs
RUN ./gradlew --offline -Pversion=${app_version} clean build

ENTRYPOINT ["./gradlew", "--offline", "endToEndTest", "--info"]
