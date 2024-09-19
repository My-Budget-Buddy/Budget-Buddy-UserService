# FROM 924809052459.dkr.ecr.us-east-1.amazonaws.com/maven as build

FROM maven:3.8-openjdk-17 AS build

WORKDIR /app

COPY . /app

RUN mvn clean package -DskipTests

FROM alpine:latest

RUN apk add --no-cache openjdk17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]