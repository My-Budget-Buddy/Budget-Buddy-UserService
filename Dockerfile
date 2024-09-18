# FROM 924809052459.dkr.ecr.us-east-1.amazonaws.com/maven as build
FROM maven:3.9.9-amazoncorretto-17 as build
WORKDIR /app
COPY . /app
RUN mvn clean install -DskipTests

FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]