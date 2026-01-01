#!/bin/bash
# wait-for-mysql.sh - Wait for MySQL to be ready with retry logic

HOST=${1:-mysql}
PORT=${2:-3306}
USER=${3:-root}
PASSWORD=${4:-root}
TIMEOUT=${5:-60}

wait_for_mysql() {
  local start_time=$(date +%s)
  local end_time=$((start_time + TIMEOUT))

  while [ $(date +%s) -lt $end_time ]; do
    if mysqladmin ping -h"$HOST" -P"$PORT" -u"$USER" -p"$PASSWORD" --silent 2>/dev/null; then
      echo "✅ MySQL is ready at $HOST:$PORT"
      return 0
    fi
    echo "⏳ Waiting for MySQL to be ready... ($(($(date +%s) - start_time))s)"
    sleep 3
  done

  echo "❌ MySQL did not become ready within ${TIMEOUT}s"
  return 1
}

echo "🚀 Waiting for MySQL at $HOST:$PORT (timeout: ${TIMEOUT}s)"
wait_for_mysql

