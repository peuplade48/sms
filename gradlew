#!/bin/sh

# Proje dizinini belirle
APP_HOME=$(dirname "$0")
APP_HOME=$(cd "$APP_HOME" && pwd)

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Java komutunu bul
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set."
fi

# Jar dosyasının yolunu tanımla
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# ÖNEMLİ: Parametre sıralaması düzeltildi. 
# "$@" (assembleDebug) en sonda olmalı.
exec "$JAVACMD" "-Xmx64m" "-Xms64m" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
