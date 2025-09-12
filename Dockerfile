# Imagen base con Maven y JDK 21
FROM maven:3.9.3-eclipse-temurin-21 AS build
WORKDIR /app
COPY backend/mini-pos/ .
RUN mvn -B -DskipTests -Dproject.build.sourceEncoding=UTF-8 package

# Imagen final con JDK 21
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
