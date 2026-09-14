#!/bin/bash
# Script para compilar y ejecutar el Sistema de Gestión de Gimnasio
# Ejecutar desde la carpeta GymMVC/

set -e

CP="lib/flatlaf-3.4.jar:lib/sqlite-jdbc-3.45.3.0.jar:lib/itextpdf-5.5.13.3.jar:lib/slf4j-api-2.0.9.jar:lib/slf4j-simple-2.0.9.jar"

echo "=== Compilando fuentes... ==="
mkdir -p out
find src -name "*.java" > sources.txt
javac -cp "$CP" -d out -sourcepath src $(cat sources.txt)
echo "=== Compilacion exitosa ==="

echo "=== Iniciando aplicacion... ==="
java -cp "out:$CP" Main
