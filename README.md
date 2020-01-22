![](https://www.ga4gh.org/wp-content/themes/ga4gh-theme/gfx/GA-logo-horizontal-tag-RGB.svg)

# Service Registry Reference Implementation [![Build Status](https://travis-ci.org/ga4gh-discovery/ga4gh-service-registry-impl.svg?branch=develop)](https://travis-ci.org/ga4gh-discovery/ga4gh-service-registry-impl) [![](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/ga4gh-discovery/ga4gh-service-registry-impl/develop/LICENSE)

This repository contains a reference implementation of the [Service Registry standard](https://github.com/ga4gh-discovery/ga4gh-service-registry/).

You can view the Service Registry API [on GitHub](https://github.com/ga4gh-discovery/ga4gh-service-registry/blob/develop/service-registry.yaml) directly, or in [Swagger Editor](https://editor.swagger.io/?url=https://raw.githubusercontent.com/ga4gh-discovery/ga4gh-service-registry/develop/service-registry.yaml).

## How to build

Natively:

```
./gradlew clean build bootRun
```

With Docker:

```
docker build -t service-registry . && docker run -p 8080:8080 -it service-registry
```

## How to test

To run all integration tests:

```
./gradlew clean build integrationTest
```

To run a subset of integration tests:

```
./gradlew clean build integrationTest --tests "*getServiceNodeById*"
```
