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

## Docker

To start the server via docker, please run the following commands:
```sh
docker pull openapitools/openapi-petstore
docker run -d -e OPENAPI_BASE_PATH=/v3 -p 80:8080 openapitools/openapi-petstore
```

Ref: https://hub.docker.com/r/openapitools/openapi-petstore/

## Security

### API key
Use `special-key` for endpoints protected by the API key

### OAuth2
By default the server supports the implicit and the password flow (even though only the implicit flow is described in the OAI spec)
The default credentials are:
* client-id: sample-client-id
* client-secret: secret
* username: user
* password: user

## Configuration

Spring parameters in application.properties:
* Server port : `server.port` (default=8080)
* API base path : `openapi.openAPIPetstore.base-path` (default=/v3). In the docker image the base path can also be set with the `OPENAPI_BASE_PATH` environment variable.

Environment variables:
* `DISABLE_API_KEY` : if set to "1", the server will not check the api key for the relevant endpoints.
* `DISABLE_OAUTH` : if set to "1", the server will not check for an OAuth2 access token.
