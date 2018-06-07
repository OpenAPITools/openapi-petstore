# OpenAPI petstore

This is an implementation of the OpenAPI pet store based on Spring-Boot.

## Overview

Start your server as a simple Spring-Boot application
```
mvn spring-boot:run
```
Or package it then run it as a Java application
```
mvn package
java -jar target/openapi-petstore-{VERSION}.jar
```

You can view the api documentation in swagger-ui by pointing to  
http://localhost:8080/


## Configuration

Spring parameters in application.properties:
* Server port : `server.port` (default=8080)
* API base path : `openapi.openAPIPetstore.base-path` (default=/v3). In the docker image the base path can also be set by the `OPENAPI_BASE_PATH` environment variable.

Environment variables:
* `DISABLE_API_KEY` : if set to "1", the server will not check the api key for the relevant endpoints.