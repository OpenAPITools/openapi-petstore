FROM openjdk:8-jre-alpine

WORKDIR /petstore

COPY target/openapi-petstore-3.0.0.jar /petstore/openapi-petstore.jar

EXPOSE 8080

CMD ["java", "-jar", "/petstore/openapi-petstore.jar"]
