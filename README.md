![](https://www.ga4gh.org/wp-content/themes/ga4gh-theme/gfx/GA-logo-horizontal-tag-RGB.svg)

# Brian's Instructions for a Mac

# run Postgres
docker run -d -p 5432:5432 --name serviceregistry -e POSTGRES_USER=serviceregistry -e POSTGRES_PASSWORD=serviceregistry postgres 

# export Liquibase name
export LIQUIBASE_DOCKER_IMAGE=docker.io/liquibase/liquibase:latest

# run the liquibase to setup schema 
% docker run --rm -it -v $(pwd)/ci/predeploy/:/liquibase/changelog/ \
  $LIQUIBASE_DOCKER_IMAGE \
  --driver=org.postgresql.Driver \
  --changeLogFile=/db.changelog.yml \
  --url=jdbc:postgresql://host.docker.internal/serviceregistry \
  --username=serviceregistry \
  --password=serviceregistry \
  update

# changes to code 
diff --git a/ci/impl/Dockerfile b/ci/impl/Dockerfile
index 06e4c44..d410872 100644
--- a/ci/impl/Dockerfile
+++ b/ci/impl/Dockerfile
@@ -12,6 +12,6 @@ FROM openjdk:11-jdk-slim

 COPY --from=builder /home/gradle/src/target/build/**/*.jar /app.jar

-EXPOSE 8080
+EXPOSE 8085

-ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar
\ No newline at end of file
+ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar
diff --git a/src/main/resources/application.yml b/src/main/resources/application.yml
index 31411b8..ce9f9d1 100644
--- a/src/main/resources/application.yml
+++ b/src/main/resources/application.yml
@@ -18,7 +18,7 @@ spring:
       password: dev
   datasource:
     driver-class-name: org.postgresql.Driver
-    url: jdbc:postgresql://localhost/serviceregistry
+    url: jdbc:postgresql://host.docker.internal/serviceregistry
     username: serviceregistry
     password: serviceregistry
     type: com.zaxxer.hikari.HikariDataSource
@@ -54,4 +54,4 @@ app:
 logging:
   level:
     org.jdbi: INFO
-    com.dnastack.discovery.registry: DEBUG
\ No newline at end of file
+    com.dnastack.discovery.registry: DEBUG￼

# build the docker image
./ci/build-docker-image dnastack-service-registry dnastack-service-registry 1.0.0

# run the docker image 
docker run -p 8085:8085 --rm -it dnastack-service-registry:latest

# open this in the browser, should be empty
http://127.0.0.1:8085/services

# At this point it’s working… but there are no entries … add with:

curl --request POST \
  --url http://localhost:8085/services \
  --header 'Authorization: Basic ZGV2OmRldg==' \
  --header 'Content-Type: application/json' \
  --data '{
        "name":"test",
        "type": {
                "group":"foo",
                "artifact":"bar",
                "version":"1.0.0"
        },
        "url":"http://localhost",
        "organization": {
                "name":"elwazi"
        },
        "version": "1.0.0"
}'

A CRUD style implementation of the GA4GH Service Registry specification. Could be used to back any kind of installation
as long as there is a compatible client that can create, update, and remove service instances when necessary.

# Service Registry Reference Implementation [![Build Status](https://travis-ci.org/ga4gh-discovery/ga4gh-service-registry-impl.svg?branch=develop)](https://travis-ci.org/ga4gh-discovery/ga4gh-service-registry-impl) [![](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/ga4gh-discovery/ga4gh-service-registry-impl/develop/LICENSE)

This repository contains a reference implementation of the [Service Registry standard](https://github.com/ga4gh-discovery/ga4gh-service-registry/).

## Development prerequisites

* OpenJDK 11 or newer. [AdoptOpenJDK](https://adoptopenjdk.net/) is a good source.
* A PostgreSQL database v9.2 or newer (we use the JSON data type). You can just install the `postgres` package on
    your OS and configure a user and a database:
    ```
    createuser -d serviceregistry
    createdb -U serviceregistry serviceregistry
    ```
* Or you can use this Docker incantation to get one temporarily:
    ```
    docker run -d -p 5432:5432 --name serviceregistry -e POSTGRES_USER=serviceregistry -e POSTGRES_PASSWORD=serviceregistry postgres
    ```

## Creating or updating the database schema

Mac or Windows:
```
docker run --rm -it -v $(pwd)/ci/predeploy/:/liquibase/changelog/ \
  $LIQUIBASE_DOCKER_IMAGE \
  --driver=org.postgresql.Driver \
  --changeLogFile=/liquibase/changelog/db.changelog.yml \
  --url=jdbc:postgresql://host.docker.internal/serviceregistry \
  --username=serviceregistry \
  --password=serviceregistry \
  update
```

Linux:
```
docker run --rm -it -v $(pwd)/ci/predeploy/:/liquibase/changelog/ \
  $LIQUIBASE_DOCKER_IMAGE \
  --driver=org.postgresql.Driver \
  --changeLogFile=/liquibase/changelog/db.changelog.yml \
  --url=jdbc:postgresql://localhost/serviceregistry \
  --username=serviceregistry \
  --password=serviceregistry \
  update
```

where $LIQUIBASE_DOCKER_IMAGE should be set to your Liquibase Docker Image link. See https://cloud.google.com/container-registry/docs/quickstart for Container Registry setup and look up your image at at https://console.cloud.google.com/gcr/images/. This value should be of form `gcr.io/container-store/liquibase-docker-image:version`.

# Running the service locally

Make sure the database schema is up-to-date, then:

```
./gradlew clean build bootRun
```

## Building a deployable Docker image

Normally the CI server runs this to build the image, but you can do it on your local machine
to test the shell script and Dockerfile.

```shell script
image_name=dnastack-service-registry
image_version=$(git describe)
docker_tag=my.docker.repo/${image_name}:{image_version}
ci/build-docker-image ${docker_tag} ${image_name} ${image_version}
```

## Running E2E tests

To run the tests, execute the following from `e2e-tests` directory:

```
./gradlew endToEndTest
```

Environment variables for development setup as required by the tests can be obtained by sourcing `test-secrets.env`. Use `set -a` to export variables so that they're accessible by `gradlew`.
