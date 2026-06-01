# Usa una imagen base de Java para construir la aplicación
FROM eclipse-temurin:17-jdk-jammy as builder

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos de Gradle y el código fuente
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Haz que el script gradlew sea ejecutable
RUN chmod +x gradlew

# Construye la aplicación Spring Boot
RUN ./gradlew bootJar

# Usa una imagen base más ligera para la aplicación final
FROM eclipse-temurin:21-jre-jammy

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR construido desde la etapa 'builder'
COPY --from=builder /app/build/libs/*.jar app.jar

# Expone el puerto que usa tu aplicación Spring Boot (por defecto 8080)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
