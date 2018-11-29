# Discovery Service Registry

## API docs

See api [documentation](https://github.com/ga4gh-discovery/ga4gh-discovery-service-registry/blob/develop/service-registry.yml). You can use [online swagger editor](https://editor.swagger.io/) to view api documentation.

## Development

```
gradle clean build bootRun
```

Or

```
docker build -t discovery-service-registry . && docker run -p 8080:8080 -it discovery-service-registry
```

## Running tests

To run entire suite of integration tests:

```
gradle clean build integrationTest
```

To run just selection of tests:

```
gradle clean build integrationTest --tests "*getServiceNodeById*"
```