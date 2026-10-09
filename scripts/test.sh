#!/usr/bin/env bash
# Offline JVM test double, no Android device / SDK required.
set -Eeuo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT="$ROOT/build/jvm-test"
mkdir -p "$OUT"
mapfile -t sources < <(find "$ROOT/src/main/java" "$ROOT/src/test/java" -type f -name '*.java' | LC_ALL=C sort)
javac --release 8 -encoding UTF-8 -d "$OUT" "${sources[@]}"
java -cp "$OUT" TestHarness
