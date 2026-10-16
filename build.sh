#!/usr/bin/env bash
set -e

echo "🔨 Compiling Java Airline Reservation Engine..."
mkdir -p bin
javac -d bin $(find src -name "*.java")
echo "✅ Compilation successful! Class files generated in bin/"
