# Usa una imagen base de Java para construir la aplicación
FROM eclipse-temurin:21-jdk-jammy as builder

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# 1. Copiar solo los archivos necesarios para descargar dependencias
# Esto optimiza el caché de Docker; las dependencias no se descargarán a menos que cambie build.gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Haz que el script gradlew sea ejecutable
RUN chmod +x gradlew

# Descargar dependencias (truco: ejecutar build sin el código fuente para cachear librerías)
RUN ./gradlew dependencies --no-daemon

# 2. Ahora copiar el código fuente y construir el JAR
COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# 3. Extraer el JAR en capas (Layered JAR optimization).
# Usamos una expansión de shell para encontrar el JAR ejecutable principal (excluyendo el 'plain' jar).
# El comando 'find' garantiza que encontremos el archivo correcto sin importar si termina en SNAPSHOT o en un número.
RUN java -Djarmode=tools \
    -jar $(find build/libs/ -name "*.jar" ! -name "*-plain.jar") \
    extract --layers --launcher --destination extracted

# Usa una imagen base más ligera para la aplicación final
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# 4. Seguridad: Crear un usuario no-root para ejecutar la aplicación
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# 5. Copiar las capas extraídas desde el builder
# El orden importa: de lo que menos cambia a lo que más cambia
COPY --chown=spring:spring --from=builder /app/extracted/dependencies/ ./
COPY --chown=spring:spring --from=builder /app/extracted/spring-boot-loader/ ./
COPY --chown=spring:spring --from=builder /app/extracted/snapshot-dependencies/ ./
COPY --chown=spring:spring --from=builder /app/extracted/application/ ./

# Expone el puerto que usa tu aplicación Spring Boot (por defecto 8080)
EXPOSE 8080

# 6. Usar el lanzador de capas de Spring Boot en lugar de java -jar
# Esto mejora el tiempo de arranque y el uso de memoria
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
