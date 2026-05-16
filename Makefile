.PHONY: all up down down-volumes build logs restart clean

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

# Muestra los logs de todos los servicios en tiempo real
logs:
	@echo "Mostrando logs de los servicios..."
	podman-compose -f $(COMPOSE_FILE) logs -f

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
	@echo "  down          - Detiene y elimina los contenedores y la red."
	@echo "  down-volumes  - Detiene y elimina los contenedores, la red y los volúmenes de datos (¡BORRA LA BD!)."
	@echo "  build         - Construye solo la imagen de la aplicación."
	@echo "  logs          - Muestra los logs de todos los servicios en tiempo real."
	@echo "  restart       - Reinicia el servicio de la aplicación."
	@echo "  exec cmd=\"<comando>\" - Ejecuta un comando dentro del contenedor de la aplicación (ej. make exec cmd=\"ls -l /app\")."
	@echo "  clean         - Limpia los artefactos de construcción de Gradle localmente."
	@echo "  help          - Muestra esta ayuda."
