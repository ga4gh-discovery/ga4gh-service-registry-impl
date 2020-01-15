# Discovery Service Registry

A CRUD style implementation of the GA4GH Service Registry specification. Could be used to back any kind of installation
as long as there is a compatible client that can create, update, and remove service instances when necessary.

## API docs

See api [documentation](https://github.com/ga4gh-discovery/ga4gh-discovery-service-registry/blob/develop/service-registry.yml). You can use [online swagger editor](https://editor.swagger.io/) to view api documentation.

## Development prerequisites

* OpenJDK 11 or newer. [AdoptOpenJDK](https://adoptopenjdk.net/) is a good source.
* A PostgreSQL database v9.2 or newer (we use the JSON data type). You can just install the `postgres` package on
    your OS, or you can use this Docker incantation to get one temporarily:
    ```
    docker run -d -p 5432:5432 --name serviceregistry -e POSTGRES_USER=serviceregistry -e POSTGRES_PASSWORD=serviceregistry postgres
    ```

## Creating or updating the database schema

Mac or Windows:
```
docker run --rm -it -v $(pwd)/ci/predeploy/:/liquibase/changelog/ \
  gcr.io/dnastack-container-store/liquibase-docker-image:1.0-2-gf48af98 \
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
  gcr.io/dnastack-container-store/liquibase-docker-image:1.0-2-gf48af98 \
  --driver=org.postgresql.Driver \
  --changeLogFile=/liquibase/changelog/db.changelog.yml \
  --url=jdbc:postgresql://localhost/serviceregistry \
  --username=serviceregistry \
  --password=serviceregistry \
  update
```

# Running the service locally

Make sure the database schema is up-to-date, then:

```
./gradlew clean build bootRun
```

## Running tests

To run entire suite of integration tests:

```
./gradlew clean build integrationTest
```

To run just selection of tests:

```
./gradlew clean build integrationTest --tests "*getServiceNodeById*"
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
