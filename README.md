# Service Registry 

## Overview
RESTful APIs to facilitate registry services for underlying service nodes (e.g., Beacon node, GA4GH DOS node etc.)

## Documentation

#### References
- [DOS Schema](https://github.com/ga4gh/data-object-service-schemas)
- [DOS Connect](https://github.com/ohsu-comp-bio/dos_connect)

#### API
- [OpenAPI Specs](https://github.com/idatamarc/dos-registry/blob/develop/src/main/resources/api.yml)

#### Build
    ./gradlew clean build

#### Running on Local

    ./gradlew bootRun -Dspring.profiles.active=local

Remote debug:

    ./gradlew bootRun  -Dspring.profiles.active=local --debug-jvm

#### Build as docker image
    ./gradlew clean build docker

#### Running as docker-compose

Developer Notes: As running this dos-registry requires dependencies on keycloak as the authentcation and authorization 
provider, as well as a persistence layer (e.g.mysql), please leverage on the docker-compose.yml in this project for
demo purposes.

    docker-compose -f ./docker-compose.yml up 