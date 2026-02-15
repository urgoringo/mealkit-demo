#!/usr/bin/env bash
set -euo pipefail

RUNS=5

usage() {
  cat <<'EOF'
Usage: benchmark-gradle.sh [--runs N|-n N]

Runs "./gradlew clean test --rerun-tasks" N times (default 5) and prints
the median runtime in seconds.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --runs|-n)
      if [[ $# -lt 2 ]]; then
        echo "Error: missing value for $1" >&2
        usage
        exit 1
      fi
      RUNS="$2"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Error: unknown argument: $1" >&2
      usage
      exit 1
      ;;
  esac
done

if ! [[ "$RUNS" =~ ^[1-9][0-9]*$ ]]; then
  echo "Error: runs must be a positive integer, got '$RUNS'" >&2
  exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [[ -x "$SCRIPT_DIR/gradlew" ]]; then
  ROOT_DIR="$SCRIPT_DIR"
elif [[ -x "$SCRIPT_DIR/../gradlew" ]]; then
  ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
else
  echo "Error: could not find gradlew relative to script location: $SCRIPT_DIR" >&2
  exit 1
fi
cd "$ROOT_DIR"

ENV_JAVA="$ROOT_DIR/scripts/env-java.sh"
if [[ -f "$ENV_JAVA" ]]; then
  # shellcheck disable=SC1090
  source "$ENV_JAVA"
fi

time_file="$(mktemp)"
sorted_file="$(mktemp)"
cleanup() {
  rm -f "$time_file" "$sorted_file"
}
trap cleanup EXIT

durations=()

echo "Running benchmark: ./gradlew clean test --rerun-tasks"
echo "Total runs: $RUNS"

for ((i = 1; i <= RUNS; i++)); do
  echo "Run $i/$RUNS..."
  /usr/bin/time -p -o "$time_file" ./gradlew clean test --rerun-tasks

  real_seconds="$(awk '$1 == "real" { print $2; exit }' "$time_file")"
  if [[ -z "$real_seconds" ]]; then
    echo "Error: failed to parse elapsed time for run $i" >&2
    exit 1
  fi

  durations+=("$real_seconds")
  printf 'Run %d time: %.3f s\n' "$i" "$real_seconds"
done

printf '%s\n' "${durations[@]}" | sort -n >"$sorted_file"

median="$(
  awk '
    { vals[NR] = $1 }
    END {
      n = NR
      if (n == 0) {
        exit 1
      }
      if (n % 2 == 1) {
        printf "%.3f", vals[(n + 1) / 2]
      } else {
        printf "%.3f", (vals[n / 2] + vals[n / 2 + 1]) / 2
      }
    }
  ' "$sorted_file"
)"

echo "Median runtime over $RUNS runs: $median s"
