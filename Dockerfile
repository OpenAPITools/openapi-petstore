FROM openjdk:8-jre-alpine

WORKDIR /petstore

ENV OPENAPI_BASE_PATH=/v3

COPY target/openapi-petstore.jar /petstore/openapi-petstore.jar

EXPOSE 8080

CMD ["java", "-Dopenapi.openAPIPetstore.base-path=${OPENAPI_BASE_PATH}", "-jar", "/petstore/openapi-petstore.jar"]
