# Beacon Service Registry 

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


#### Operation
