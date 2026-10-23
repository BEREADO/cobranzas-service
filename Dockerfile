FROM bellsoft/liberica-openjdk-alpine:25
WORKDIR /app
COPY target/egresos-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
