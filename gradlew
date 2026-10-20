#!/usr/bin/env sh
set -eu
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION=8.9
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
GRADLE_HOME="$GRADLE_USER_HOME/manual-wrapper/gradle-$GRADLE_VERSION"
GRADLE_BIN="$GRADLE_HOME/bin/gradle"
if [ ! -x "$GRADLE_BIN" ]; then
  mkdir -p "$GRADLE_USER_HOME/manual-wrapper"
  ARCHIVE="$GRADLE_USER_HOME/manual-wrapper/gradle-$GRADLE_VERSION-bin.zip"
  if command -v curl >/dev/null 2>&1; then
    curl -fL --retry 3 "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$ARCHIVE"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$ARCHIVE" "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
  else
    echo "Error: curl or wget is required to download Gradle." >&2
    exit 1
  fi
  command -v unzip >/dev/null 2>&1 || { echo "Error: unzip is required." >&2; exit 1; }
  unzip -q -o "$ARCHIVE" -d "$GRADLE_USER_HOME/manual-wrapper"
fi
exec "$GRADLE_BIN" -p "$APP_HOME" "$@"
