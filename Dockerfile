FROM openjdk:8-jre-alpine

WORKDIR /petstore

ENV OPENAPI_BASE_PATH=/v3

COPY target/openapi-petstore-3.0.0.jar /petstore/openapi-petstore.jar

EXPOSE 8088

CMD ["java", "-Dopenapi.openAPIPetstore.base-path=${OPENAPI_BASE_PATH}", "-jar", "/petstore/openapi-petstore.jar"]
