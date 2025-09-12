# ====== Imagen base con JDK y Maven ======
FROM maven:3.9.3-eclipse-temurin-17 AS build

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar solo la carpeta del backend donde está el pom.xml
COPY backend/mini-pos/ .

# Construir el proyecto con Maven
RUN mvn -B -DskipTests package

# ====== Imagen final con JRE para ejecutar la app ======
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copiar el .jar generado desde la fase build
COPY --from=build /app/target/*.jar app.jar

# Puerto de la app (ajusta según tu configuración)
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
