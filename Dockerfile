FROM bellsoft/liberica-openjdk-alpine:25
WORKDIR /app
COPY target/cobranzas-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
