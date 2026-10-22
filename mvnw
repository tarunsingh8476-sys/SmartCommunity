#!/bin/sh
set -eu

MAVEN_VERSION="3.9.9"
WRAPPER_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
MAVEN_USER_HOME="${MAVEN_USER_HOME:-${HOME:-$WRAPPER_DIR}/.m2}"
CACHE_DIR="$MAVEN_USER_HOME/wrapper/dists/apache-maven-$MAVEN_VERSION"
MAVEN_HOME="$CACHE_DIR/apache-maven-$MAVEN_VERSION"
MAVEN_BIN="$MAVEN_HOME/bin/mvn"

if [ ! -x "$MAVEN_BIN" ]; then
  mkdir -p "$CACHE_DIR"
  ZIP="$CACHE_DIR/apache-maven-$MAVEN_VERSION-bin.zip"
  URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.zip"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$URL" -o "$ZIP"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$URL" -O "$ZIP"
  else
    echo "Maven Wrapper needs curl or wget to download Maven." >&2
    exit 1
  fi
  if ! command -v unzip >/dev/null 2>&1; then
    echo "Maven Wrapper needs unzip to extract Maven." >&2
    exit 1
  fi
  unzip -q -o "$ZIP" -d "$CACHE_DIR"
  rm -f "$ZIP"
fi

if [ ! -x "$MAVEN_BIN" ]; then
  echo "Could not find Maven at $MAVEN_BIN after extraction." >&2
  exit 1
fi
exec "$MAVEN_BIN" "$@"
