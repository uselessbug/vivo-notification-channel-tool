#!/usr/bin/env bash
# Build a minimal classes.dex archive for Android's app_process using the official D8 tool.
set -Eeuo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD="$ROOT/build"
DIST="$ROOT/dist"
BUILD_TOOLS_VERSION="${BUILD_TOOLS_VERSION:-36.0.0}"
ANDROID_SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"

if [[ -z "$ANDROID_SDK" ]]; then
    echo 'Error: set ANDROID_HOME or ANDROID_SDK_ROOT to the installed Android SDK.' >&2
    exit 1
fi
D8="$ANDROID_SDK/build-tools/$BUILD_TOOLS_VERSION/d8"
ANDROID_JAR="$ANDROID_SDK/platforms/android-36/android.jar"
if [[ ! -x "$D8" || ! -f "$ANDROID_JAR" ]]; then
    echo "Error: install Android SDK build-tools;$BUILD_TOOLS_VERSION and platforms;android-36." >&2
    exit 1
fi

rm -rf "$BUILD" "$DIST"
mkdir -p "$BUILD/classes" "$BUILD/dex" "$DIST"

# Stubs are used ONLY for compilation. Importantly, they are not bundled into DEX.
mapfile -t stub_files < <(find "$ROOT/src/stubs/java" -type f -name '*.java' | LC_ALL=C sort)
javac --release 8 -encoding UTF-8 -d "$BUILD/classes" \
    "${stub_files[@]}" "$ROOT/src/main/java/VivoNotificationChannelTool.java"

# Compile ONLY the tool's class (and not the android.* compile-time stub classes).
"$D8" --min-api 26 --lib "$ANDROID_JAR" \
    --classpath "$BUILD/classes" \
    --output "$BUILD/dex" "$BUILD/classes/VivoNotificationChannelTool.class"

python3 "$ROOT/scripts/package.py" \
    "$BUILD/dex/classes.dex" "$DIST/vivo-development-mode-channel.jar"
python3 "$ROOT/scripts/verify.py" "$DIST/vivo-development-mode-channel.jar"
(
    cd "$DIST"
    sha256sum vivo-development-mode-channel.jar > SHA256SUMS
)
echo "Built: $DIST/vivo-development-mode-channel.jar"
cat "$DIST/SHA256SUMS"
