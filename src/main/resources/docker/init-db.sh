#!/bin/bash

# Iniciar la base de datos
podman-compose up -d db

# Esperar a que la base de datos esté lista
sleep 15

# Ejecutar las sentencias SQL de inicialización
psql -h localhost -U pos_user -d pos_db -p 5432 -a -f src/main/resources/db/init.sql

echo "Base de datos inicializada correctamente"