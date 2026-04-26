#!/bin/sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
SERVER_DIR=${1:-"$SCRIPT_DIR/mindustry-server-v7"}
RESTART_DELAY_SECONDS=${RESTART_DELAY_SECONDS:-2}

SERVER_JAR=""
if [ -f "$SERVER_DIR/server-release.jar" ]; then
    SERVER_JAR="$SERVER_DIR/server-release.jar"
else
    SERVER_JAR=$(find "$SERVER_DIR" -maxdepth 1 -type f -name 'server-release-*.jar' | sort | tail -n 1 || true)
fi

if [ -z "$SERVER_JAR" ]; then
    echo "No Mindustry server jar found in $SERVER_DIR" >&2
    exit 1
fi

echo "Hosting Mindustry server from $SERVER_DIR"
echo "Using server jar $SERVER_JAR"
echo "Press Ctrl+C to stop the host loop."

cd "$SERVER_DIR"
while true; do
    echo "Starting server at $(date '+%Y-%m-%d %H:%M:%S')"
    java -jar "$SERVER_JAR"
    exit_code=$?

    if [ "$exit_code" -eq 0 ]; then
        echo "Server exited cleanly. Restarting in $RESTART_DELAY_SECONDS second(s)..."
    else
        echo "Server exited with code $exit_code. Restarting in $RESTART_DELAY_SECONDS second(s)..." >&2
    fi

    sleep "$RESTART_DELAY_SECONDS"
done