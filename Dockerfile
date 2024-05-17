FROM eclipse-temurin:17-jre-alpine
RUN apk update && \
    apk upgrade
WORKDIR /app
COPY target/user_service-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]
