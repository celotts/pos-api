# --- Etapa de Construcción (Builder) ---
# Usa una imagen base con JDK para compilar la aplicación
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Copia los archivos de Gradle y descarga dependencias (aprovecha el cache de Docker)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon

# Copia el código fuente y construye el JAR
COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# --- Etapa Final (Runner) ---
# Usa una imagen base más ligera solo con JRE para ejecutar la aplicación
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]