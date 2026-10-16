#!/usr/bin/env bash
set -e

# Compile if bin directory does not exist
if [ ! -d "bin" ]; then
    ./build.sh
fi

case "$1" in
    --demo|-d|demo)
        java -cp bin com.airline.Main --demo
        ;;
    --walkthrough|-w|walkthrough)
        java -cp bin com.airline.Main --walkthrough
        ;;
    --test|-t|test)
        java -cp bin com.airline.test.TestRunner
        ;;
    --server|-s|server)
        PORT=${2:-8080}
        java -cp bin com.airline.Main --server "$PORT"
        ;;
    *)
        java -cp bin com.airline.Main "$@"
        ;;
esac
