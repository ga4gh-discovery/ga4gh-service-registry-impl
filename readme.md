# GA4GH Data-Object-Service(DOS) Registry 

## Overview
RESTful APIs to facilitate registry services for underlying DOS nodes

### Contact information
*Contact* : DNAStack Support Team  
*Contact Email* : support@dnastack.com


## Documentation

#### References

- [DOS Schema](https://github.com/ga4gh/data-object-service-schemas)
- [DOS Connect](https://github.com/ohsu-comp-bio/dos_connect)

#### API
- [OpenAPI Specs](http://dnastack.com/api.yml)

#### Build
    ./gradlew clean build

#### Running on Local

    ./gradlew bootRun -Dspring.profiles.active=local

After successfully bootRun it on local, you can view the swagger-ui [here](http://localhost:8082/swagger-ui.html)

#### Remote debug:

    ./gradlew bootRun  -Dspring.profiles.active=local --debug-jvm


#### Operation
