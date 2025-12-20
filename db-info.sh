#!/bin/bash

# Script to show PostgreSQL TestContainer connection details

CONTAINER_ID=$(docker ps --filter "ancestor=postgres:17-alpine" --format "{{.ID}}" | head -1)

if [ -z "$CONTAINER_ID" ]; then
    echo "❌ No PostgreSQL TestContainer is running"
    echo ""
    echo "Run any test to start the container:"
    echo "  ./gradlew test --tests MealkitApplicationTests"
    exit 1
fi

PORT=$(docker port "$CONTAINER_ID" 5432 | cut -d: -f2)

echo ""
echo "╔═══════════════════════════════════════════════════╗"
echo "║  PostgreSQL TestContainer Connection Info         ║"
echo "╠═══════════════════════════════════════════════════╣"
echo "║  Host:     localhost                              ║"
echo "║  Port:     $PORT                                   ║"
echo "║  Database: test                                   ║"
echo "║  Username: test                                   ║"
echo "║  Password: test                                   ║"
echo "╠═══════════════════════════════════════════════════╣"
echo "║  Connection String:                               ║"
echo "║  jdbc:postgresql://localhost:$PORT/test           ║"
echo "╠═══════════════════════════════════════════════════╣"
echo "║  Connect via psql:                                ║"
echo "║  psql -h localhost -p $PORT -U test -d test       ║"
echo "╚═══════════════════════════════════════════════════╝"
echo ""
