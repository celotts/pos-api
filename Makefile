.PHONY: all up down down-volumes build logs logs-app logs-db restart clean app-only help

# Variables
COMPOSE_FILE := podman-compose.yaml
APP_NAME := pos-api
DB_SERVICE := db
APP_SERVICE := app

all: up

# Levanta los contenedores (construye la imagen de la app si es necesario)
up:
	@echo "Levantando contenedores..."
	podman-compose -f $(COMPOSE_FILE) up --build -d
	@echo "Contenedores levantados en segundo plano."
	@echo "Para ver los logs, usa 'make logs'"

# Levanta solo la aplicación, asumiendo que el contenedor de la BD ya está corriendo.
# Fallará si el contenedor de la BD no está ya corriendo.
app-only:
	@echo "Intentando levantar solo el servicio de la aplicación $(APP_SERVICE)..."
	@if ! podman-compose -f $(COMPOSE_FILE) ps -q $(DB_SERVICE) | grep -q .; then \
		echo "Error: El contenedor de la base de datos ($(DB_SERVICE)) no está corriendo. Por favor, use 'make up' para iniciar todo el entorno."; \
		exit 1; \
	fi
	@echo "El contenedor de la base de datos ($(DB_SERVICE)) está corriendo. Iniciando la aplicación..."
	podman-compose -f $(COMPOSE_FILE) up -d --no-deps $(APP_SERVICE)
	@echo "Servicio de la aplicación $(APP_SERVICE) levantado en segundo plano."
	@echo "Para ver los logs de la aplicación, usa 'make logs-app'"

# Detiene y elimina los contenedores y la red
down:
	@echo "Deteniendo y eliminando contenedores..."
	podman-compose -f $(COMPOSE_FILE) down

# Detiene y elimina los contenedores, la red y los volúmenes de datos
# ¡CUIDADO! Esto borrará todos los datos de tu base de datos.
down-volumes:
	@echo "Deteniendo y eliminando contenedores, red y VOLÚMENES DE DATOS..."
	podman-compose -f $(COMPOSE_FILE) down -v
	@echo "Verificando y eliminando volumen de datos de la base de datos..."
	-podman volume rm $(APP_NAME)_db_data || true # Elimina explícitamente el volumen
	@echo "Volumen de datos de la base de datos eliminado (si existía)."

# Construye solo la imagen de la aplicación
build:
	@echo "Construyendo imagen de la aplicación $(APP_SERVICE)..."
	podman-compose -f $(COMPOSE_FILE) build $(APP_SERVICE)

# Muestra los logs de todos los servicios en tiempo real (puede fallar en remoto)
logs:
	@echo "Mostrando logs de todos los servicios..."
	podman-compose -f $(COMPOSE_FILE) logs -f

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
	@echo "Ejecutando comando en el contenedor $(APP_SERVICE)..."
	podman-compose -f $(COMPOSE_FILE) exec $(APP_SERVICE) $(cmd)

# Limpia los artefactos de construcción locales (ej. el JAR generado por Gradle)
clean:
	@echo "Limpiando artefactos de construcción locales..."
	./gradlew clean
	@echo "Limpieza completada."

# Ayuda
help:
	@echo "Uso: make [comando]"
	@echo ""
	@echo "Comandos disponibles:"
	@echo "  all           - Alias para 'up'. Levanta los contenedores en segundo plano."
	@echo "  up            - Levanta los contenedores (construye la imagen de la app si es necesario) en segundo plano."
	@echo "  app-only      - Levanta solo el servicio de la aplicación. Requiere que el contenedor de la BD esté ya corriendo."
	@echo "  down          - Detiene y elimina los contenedores y la red."
	@echo "  down-volumes  - Detiene y elimina los contenedores, la red y los volúmenes de datos (¡BORRA LA BD!)."
	@echo "  build         - Construye solo la imagen de la aplicación."
	@echo "  logs          - Muestra los logs de todos los servicios en tiempo real (puede fallar en remoto)."
	@echo "  logs-app      - Muestra los logs solo del servicio de la aplicación."
	@echo "  logs-db       - Muestra los logs solo del servicio de la base de datos."
	@echo "  restart       - Reinicia el servicio de la aplicación."
	@echo "  exec cmd=\"<comando>\" - Ejecuta un comando dentro del contenedor de la aplicación (ej. make exec cmd=\"ls -l /app\")."
	@echo "  clean         - Limpia los artefactos de construcción de Gradle localmente."
	@echo "  help          - Muestra esta ayuda."
