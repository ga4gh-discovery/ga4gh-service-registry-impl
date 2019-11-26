# Discovery Service Registry

## API docs

See api [documentation](https://github.com/ga4gh-discovery/ga4gh-discovery-service-registry/blob/develop/service-registry.yml). You can use [online swagger editor](https://editor.swagger.io/) to view api documentation.

## Development

You will need OpenJDK 11 or newer. [AdoptOpenJDK](https://adoptopenjdk.net/) is a good source.

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
