# ===============================
# Etapa 1: Construcción con Maven
# ===============================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -B -DskipTests package

# ===============================
# Etapa 2: Imagen ligera para producción
# ===============================
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render asigna $PORT automáticamente
EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
