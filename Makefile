.PHONY: all up up-clean down down-volumes build build-fast logs logs-app logs-db \
        db-shell restart clean app-only status help test check ci run exec dev

# ─────────────────────────────────────────────
# Variables
# ─────────────────────────────────────────────
BIN          := podman
COMPOSE_BIN  := podman-compose
COMPOSE_FILE := podman-compose.yml
APP_NAME     := pos-api
DB_SERVICE   := db
APP_SERVICE  := app

STATUS_REPORT_CMD = $(BIN) ps --filter "name=pos" --format "{{.Names}}|{{.Status}}|{{.Ports}}" \
  | awk -F'|' 'BEGIN {sep="===================================================================================================="; \
    print sep; printf "   %-25s %-30s %s\n", "NAMES", "STATUS", "PORTS"; print sep} \
    {icon="🔴"; if ($$2 ~ /Up/ || $$2 ~ /healthy/) icon="🟢"; \
    printf "%s %-25s %-30s %s\n", icon, $$1, $$2, $$3} END {print sep}'

# ─────────────────────────────────────────────
# DEFAULT
# ─────────────────────────────────────────────
all: help

# ─────────────────────────────────────────────
# DESARROLLO LOCAL (sin contenedores)
# ─────────────────────────────────────────────

## run: Levanta la app localmente con bootRun (requiere BD corriendo)
run:
	@echo "🚀 Iniciando aplicación localmente con bootRun..."
	./gradlew bootRun

## dev: Alias de 'run'
dev: run

## test: Ejecuta todos los tests
test:
	@echo "🧪 Ejecutando tests..."
	./gradlew test

## check: Ejecuta tests + análisis estático (checkstyle + spotbugs)
check:
	@echo "🔍 Ejecutando checks (tests + análisis estático)..."
	./gradlew check

## ci: Limpieza completa + checks (ideal para pipelines CI)
ci:
	@echo "⚙️  CI: limpieza y ejecución de checks..."
	./gradlew clean check

## clean: Limpia artefactos de construcción de Gradle
clean:
	@echo "🧹 Limpiando artefactos de Gradle..."
	./gradlew clean
	@echo "✅ Limpieza completada."

## build-fast: Compila el JAR omitiendo los tests
build-fast:
	@echo "⚡ Compilando JAR sin tests..."
	./gradlew bootJar -x test
	@echo "✅ JAR generado en build/libs/"

# ─────────────────────────────────────────────
# CONTENEDORES (Podman / podman-compose)
# ─────────────────────────────────────────────

## up: Levanta todos los contenedores preservando los datos existentes
up:
	@START_TIME=$$(date +%s); \
	echo "🐳 Levantando contenedores..."; \
	$(COMPOSE_BIN) -f $(COMPOSE_FILE) up --build -d; \
	if [ $$($(BIN) ps --filter "name=pos" --format "{{.Status}}" | grep -c "Up") -gt 0 ] && \
	   [ $$($(BIN) ps --filter "name=pos" --format "{{.Status}}" | grep -cvE "Up|healthy") -eq 0 ]; then \
	   echo "\n🚀 Entorno levantado exitosamente\n"; \
	else \
	   echo "\n⚠️  Algunos servicios presentan problemas\n"; \
	fi; \
	$(STATUS_REPORT_CMD); \
	echo "💡 Para ver los logs usa: make logs\n"

## up-clean: Elimina volúmenes anteriores y levanta desde cero (reinicia la BD)
up-clean: down-volumes up

## app-only: Levanta solo la app asumiendo que la BD ya está corriendo
app-only:
	@echo "🔄 Intentando levantar solo el servicio $(APP_SERVICE)..."; \
	if ! $(COMPOSE_BIN) -f $(COMPOSE_FILE) ps -q $(DB_SERVICE) | grep -q .; then \
	   echo "❌ Error: el contenedor de la BD ($(DB_SERVICE)) no está corriendo."; \
	   echo "   Usa 'make up' para iniciar todo el entorno."; \
	   exit 1; \
	fi; \
	echo "✅ BD detectada. Iniciando la aplicación..."; \
	$(COMPOSE_BIN) -f $(COMPOSE_FILE) up -d --no-deps $(APP_SERVICE); \
	echo "\n🚀 Servicio $(APP_SERVICE) levantado\n"; \
	$(STATUS_REPORT_CMD); \
	echo "💡 Para ver los logs usa: make logs-app\n"

## down: Detiene y elimina los contenedores y la red
down:
	@echo "🛑 Deteniendo contenedores..."
	$(COMPOSE_BIN) -f $(COMPOSE_FILE) down
	@echo "✅ Contenedores detenidos."

## down-volumes: Detiene contenedores y elimina volúmenes de datos (irreversible)
down-volumes:
	@echo "🔥 Eliminando contenedores y VOLÚMENES DE DATOS..."
	$(COMPOSE_BIN) -f $(COMPOSE_FILE) down -v
	@$(BIN) volume rm $(APP_NAME)_db_data 2>/dev/null || true
	@echo "✅ Contenedores, red y volúmenes eliminados."

## build: Construye la imagen Docker/Podman de la aplicación
build:
	@echo "🏗️  Construyendo imagen de $(APP_SERVICE)..."
	$(COMPOSE_BIN) -f $(COMPOSE_FILE) build $(APP_SERVICE)
	@echo "✅ Imagen construida."

## restart: Reinicia solo el servicio de la aplicación
restart:
	@echo "🔄 Reiniciando $(APP_SERVICE)..."
	$(COMPOSE_BIN) -f $(COMPOSE_FILE) restart $(APP_SERVICE)

# ─────────────────────────────────────────────
# LOGS
# ─────────────────────────────────────────────

## logs: Muestra snapshot de logs de todos los servicios
logs:
	@echo "⚠️  Mostrando snapshot de logs por servicio:\n"
	@echo "--- [ $(DB_SERVICE) ] ---"
	@$(COMPOSE_BIN) -f $(COMPOSE_FILE) logs --tail 50 $(DB_SERVICE) || echo "No se pudieron obtener logs de $(DB_SERVICE)"
	@echo "\n--- [ $(APP_SERVICE) ] ---"
	@$(COMPOSE_BIN) -f $(COMPOSE_FILE) logs --tail 50 $(APP_SERVICE) || echo "No se pudieron obtener logs de $(APP_SERVICE)"
	@echo "\n💡 Para seguir en tiempo real: make logs-app  o  make logs-db"

## logs-app: Sigue los logs de la aplicación en tiempo real
logs-app:
	$(COMPOSE_BIN) -f $(COMPOSE_FILE) logs -f $(APP_SERVICE)

## logs-db: Sigue los logs de la base de datos en tiempo real
logs-db:
	$(COMPOSE_BIN) -f $(COMPOSE_FILE) logs -f $(DB_SERVICE)

# ─────────────────────────────────────────────
# UTILIDADES
# ─────────────────────────────────────────────

## status: Muestra el estado de los contenedores
status:
	@echo "\n📊 Estado de los contenedores:"
	@$(STATUS_REPORT_CMD)

## db-shell: Abre una consola psql dentro del contenedor de la BD
db-shell:
	@echo "🐘 Conectando a la BD..."
	$(COMPOSE_BIN) -f $(COMPOSE_FILE) exec $(DB_SERVICE) \
	  psql -U $$(grep POSTGRES_USER $(COMPOSE_FILE) | awk '{print $$2}' | sed 's/[:\"-]//g' | head -1) \
	       -d $$(grep POSTGRES_DB  $(COMPOSE_FILE) | awk '{print $$2}' | sed 's/[:\"-]//g' | head -1)

## exec: Ejecuta un comando en el contenedor de la app (cmd=<comando>)
##       Sin argumentos abre una shell interactiva.
exec:
	@if [ -z "$(cmd)" ]; then \
	   echo "🛠️  Abriendo shell interactiva en $(APP_SERVICE)..."; \
	   $(COMPOSE_BIN) -f $(COMPOSE_FILE) exec $(APP_SERVICE) /bin/sh; \
	else \
	   echo "🛠️  Ejecutando en $(APP_SERVICE): $(cmd)"; \
	   $(COMPOSE_BIN) -f $(COMPOSE_FILE) exec $(APP_SERVICE) $(cmd); \
	fi

# ─────────────────────────────────────────────
# AYUDA
# ─────────────────────────────────────────────

## help: Muestra esta ayuda
help:
	@echo ""
	@echo "╔══════════════════════════════════════════════════════╗"
	@echo "║              🛒  POS API  — Makefile                 ║"
	@echo "╚══════════════════════════════════════════════════════╝"
	@echo ""
	@echo "  DESARROLLO LOCAL"
	@echo "  ─────────────────────────────────────────────────────"
	@echo "  make run          🚀 Levanta la app localmente (bootRun)"
	@echo "  make dev          🚀 Alias de 'run'"
	@echo "  make test         🧪 Ejecuta todos los tests"
	@echo "  make check        🔍 Tests + análisis estático"
	@echo "  make ci           ⚙️  Limpieza + check completo"
	@echo "  make clean        🧹 Limpia artefactos de Gradle"
	@echo "  make build-fast   ⚡ Genera JAR sin correr tests"
	@echo ""
	@echo "  CONTENEDORES"
	@echo "  ─────────────────────────────────────────────────────"
	@echo "  make up           🐳 Levanta todos los contenedores"
	@echo "  make up-clean     🔥 Limpia datos y levanta desde cero"
	@echo "  make app-only     🔄 Levanta solo la app (BD ya activa)"
	@echo "  make down         🛑 Detiene y elimina contenedores"
	@echo "  make down-volumes 🔥 Elimina contenedores + volúmenes"
	@echo "  make build        🏗️  Construye imagen Docker/Podman"
	@echo "  make restart      🔄 Reinicia el servicio de la app"
	@echo "  make status       📊 Estado actual de los contenedores"
	@echo ""
	@echo "  LOGS"
	@echo "  ─────────────────────────────────────────────────────"
	@echo "  make logs         📋 Snapshot de logs de todos los servicios"
	@echo "  make logs-app     📋 Logs en tiempo real de la app"
	@echo "  make logs-db      📋 Logs en tiempo real de la BD"
	@echo ""
	@echo "  UTILIDADES"
	@echo "  ─────────────────────────────────────────────────────"
	@echo "  make db-shell     🐘 Consola psql en el contenedor BD"
	@echo "  make exec         🛠️  Shell o comando en el contenedor app"
	@echo "                        Ej: make exec cmd='java -version'"
	@echo ""