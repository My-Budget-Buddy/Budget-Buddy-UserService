FROM eclipse-temurin:17-jre-alpine
RUN apk update && \
    apk upgrade
WORKDIR /app
COPY target/${jarFile[0]} /app/app.jar
EXPOSE ${portsTable[directory.toLowerCase()]}
CMD ["java", "-jar", "app.jar"]
