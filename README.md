# egresos-service

Microservicio Java/Spring Boot del proyecto Grupo 12.

## Compilar

```bash
mvn clean package
```

## Construir imagen Docker

```bash
docker build -t egresos-service .
```

## Ejecutar

```bash
docker run --rm -p 8086:8086 egresos-service
```

Revisa `src/main/resources/application.properties` para confirmar el puerto y las dependencias externas requeridas.
