.PHONY: all up down down-volumes build logs logs-app logs-db restart clean app-only status help

# Variables
COMPOSE_FILE := podman-compose.yaml
APP_NAME := pos-api
DB_SERVICE := db
APP_SERVICE := app

all: up

# Levanta los contenedores (construye la imagen de la app si es necesario)
up:
	@START_TIME=$$(date +%s); \
	echo "Levantando contenedores..."; \
	podman-compose -f $(COMPOSE_FILE) up --build -d; \
	if [ $$(podman ps --filter "name=pos" --format "{{.Status}}" | grep -c "Up") -gt 0 ] && \
	   [ $$(podman ps --filter "name=pos" --format "{{.Status}}" | grep -cvE "Up|healthy") -eq 0 ]; then \
		echo "\n🚀 Entorno levantado exitosamente"; \
	else \
		echo "\n⚠️  Algunos servicios presentan problemas"; \
	fi; \
	DURATION=$$(( $$(date +%s) - START_TIME )); \
	podman ps --filter "name=pos" --format "{{.Names}}|{{.Status}}|{{.Ports}}" | awk -v dur="$$DURATION" -F'|' 'BEGIN {sep="===================================================================================================="; print sep; printf "   %-25s %-30s %s\n", "NAMES", "STATUS", "PORTS"; print sep} {icon="🔴"; if ($$2 ~ /Up/ || $$2 ~ /healthy/) icon="🟢"; printf "%s %-25s %-30s %s\n", icon, $$1, $$2, $$3} END {print sep; printf "⏱️  Tiempo total de levantamiento: %s segundos\n", dur; print sep}'; \
	echo "Para ver los logs, usa 'make logs'\n"

# Levanta solo la aplicación, asumiendo que el contenedor de la BD ya está corriendo.
# Fallará si el contenedor de la BD no está ya corriendo.
app-only:
	@START_TIME=$$(date +%s); \
	echo "Intentando levantar solo el servicio de la aplicación $(APP_SERVICE)..."; \
	if ! podman-compose -f $(COMPOSE_FILE) ps -q $(DB_SERVICE) | grep -q .; then \
		echo "Error: El contenedor de la base de datos ($(DB_SERVICE)) no está corriendo. Por favor, use 'make up' para iniciar todo el entorno."; \
		exit 1; \
	fi; \
	echo "El contenedor de la base de datos ($(DB_SERVICE)) está corriendo. Iniciando la aplicación..."; \
	podman-compose -f $(COMPOSE_FILE) up -d --no-deps $(APP_SERVICE); \
	echo "\n🚀 Servicio $(APP_SERVICE) levantado"; \
	DURATION=$$(( $$(date +%s) - START_TIME )); \
	podman ps --filter "name=pos" --format "{{.Names}}|{{.Status}}|{{.Ports}}" | awk -v dur="$$DURATION" -F'|' 'BEGIN {sep="===================================================================================================="; print sep; printf "   %-25s %-30s %s\n", "NAMES", "STATUS", "PORTS"; print sep} {icon="🔴"; if ($$2 ~ /Up/ || $$2 ~ /healthy/) icon="🟢"; printf "%s %-25s %-30s %s\n", icon, $$1, $$2, $$3} END {print sep; printf "⏱️  Tiempo total de levantamiento: %s segundos\n", dur; print sep}'; \
	echo "Para ver los logs de la aplicación, usa 'make logs-app'\n"

# Detiene y elimina los contenedores y la red
down:
	@echo "Deteniendo y eliminando contenedores..."
	podman-compose -f $(COMPOSE_FILE) down

# Detiene y elimina los contenedores, la red y los volúmenes de datos
# ¡CUIDADO! Esto borrará todos los datos de tu base de datos.
down-volumes:
	@echo "🔥 Deteniendo servicios y eliminando VOLÚMENES DE DATOS..."
	podman-compose -f $(COMPOSE_FILE) down -v
	@podman volume rm $(APP_NAME)_db_data 2>/dev/null || true
	@echo "✅ Limpieza completa: Contenedores, red y volúmenes eliminados."

# Construye solo la imagen de la aplicación
build:
	@echo "Construyendo imagen de la aplicación $(APP_SERVICE)..."
	podman-compose -f $(COMPOSE_FILE) build $(APP_SERVICE)

# Muestra los logs de todos los servicios en tiempo real (puede fallar en remoto)
# Nota: En Podman remoto, no se pueden seguir (-f) múltiples contenedores a la vez.
logs:
	@echo "⚠️  Podman remoto no permite multiplexar logs de múltiples contenedores."
	@echo "Mostrando snapshot de logs por servicio:\n"
	@echo "--- [ $(DB_SERVICE) ] ---"
	@podman-compose -f $(COMPOSE_FILE) logs --tail 50 $(DB_SERVICE) || echo "No se pudieron obtener logs de $(DB_SERVICE)"
	@echo "\n--- [ $(APP_SERVICE) ] ---"
	@podman-compose -f $(COMPOSE_FILE) logs --tail 50 $(APP_SERVICE) || echo "No se pudieron obtener logs de $(APP_SERVICE)"
	@echo "\n💡 Para seguir logs en tiempo real usa: 'make logs-app' o 'make logs-db'"

# Muestra los logs solo del servicio de la aplicación
logs-app:
	@echo "Mostrando logs del servicio de la aplicación $(APP_SERVICE)..."
	podman-compose -f $(COMPOSE_FILE) logs -f $(APP_SERVICE)

# Muestra los logs solo del servicio de la base de datos
logs-db:
	@echo "Mostrando logs del servicio de la base de datos $(DB_SERVICE)..."
	podman-compose -f $(COMPOSE_FILE) logs -f $(DB_SERVICE)

# Reinicia el servicio de la aplicación
restart:
	@echo "Reiniciando el servicio de la aplicación $(APP_SERVICE)..."
	podman-compose -f $(COMPOSE_FILE) restart $(APP_SERVICE)

# Ejecuta un comando dentro del contenedor de la aplicación (ej. make exec cmd="ls -l")
exec:
	@if [ -z "$(cmd)" ]; then \
		echo "Error: Debes proporcionar un comando. Ejemplo: make exec cmd=\"ls -l\""; \
		exit 1; \
	fi
	@echo "🛠️  Ejecutando en $(APP_SERVICE): $(cmd)"
	@podman-compose -f $(COMPOSE_FILE) exec $(APP_SERVICE) $(cmd)

# Limpia los artefactos de construcción locales (ej. el JAR generado por Gradle)
clean:
	@echo "Limpiando artefactos de construcción locales..."
	./gradlew clean
	@echo "Limpieza completada."

# Compila el proyecto generando el JAR de forma ultra rápida sin ejecutar tests
build-fast:
	@echo "🚀 Compilando proyecto sin ejecutar pruebas..."
	./gradlew bootJar -x test
	@echo "✅ Compilación completada con éxito."

# Muestra el estado actual de los contenedores
status:
	@echo "\n📊 Estado actual de los contenedores:"
	@podman ps --filter "name=pos" --format "{{.Names}}|{{.Status}}|{{.Ports}}" | awk -F'|' 'BEGIN {sep="===================================================================================================="; print sep; printf "   %-25s %-30s %s\n", "NAMES", "STATUS", "PORTS"; print sep} {icon="🔴"; if ($$2 ~ /Up/ || $$2 ~ /healthy/) icon="🟢"; printf "%s %-25s %-30s %s\n", icon, $$1, $$2, $$3} END {print sep}'
# Ayuda
help:
	@echo "Uso: make [comando]"
	@echo ""
	@echo "Comandos disponibles:"
	@echo "  all           - Alias para 'up'. Levanta los contenedores en segundo plano."
	@echo "  up            - Levanta los contenedores (construye la imagen de la app si es necesario) en segundo plano."
	@echo "  app-only      - Levanta solo el servicio de la aplicación. Requiere que el contenedor de la BD esté ya corriendo."
	@echo "  status        - Muestra el resumen de estado y puertos de los contenedores."
	@echo "  down          - Detiene y elimina los contenedores y la red."
	@echo "  down-volumes  - Detiene y elimina los contenedores, la red y los volúmenes de datos (¡BORRA LA BD!)."
	@echo "  build         - Construye solo la imagen de la aplicación."
	@echo "  build-fast    - Compila el proyecto con Gradle omitiendo los tests (ahorra CPU/ventiladores)."
	@echo "  logs          - Muestra los logs de todos los servicios en tiempo real (puede fallar en remoto)."
	@echo "  logs-app      - Muestra los logs solo del servicio de la aplicación."
	@echo "  logs-db       - Muestra los logs solo del servicio de la base de datos."
	@echo "  restart       - Reinicia el servicio de la aplicación."
	@echo "  exec cmd=\"<comando>\" - Ejecuta un comando dentro del contenedor de la aplicación (ej. make exec cmd=\"ls -l /app\")."
	@echo "  clean         - Limpia los artefactos de construcción de Gradle localmente."
	@echo "  help          - Muestra esta ayuda."