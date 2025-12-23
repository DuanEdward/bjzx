#!/bin/bash

# 后端服务启动脚本

# 加载配置文件
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/config.sh"

APP_NAME="bjzxjj-website"
APP_JAR="${BACKEND_DIR}/bjzxjj-website.jar"
APP_DIR="${BACKEND_DIR}"
PID_FILE="/var/run/${APP_NAME}.pid"

# JVM参数（从配置文件读取）
JVM_OPTS="-Xms${JVM_XMS} -Xmx${JVM_XMX} -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Spring Boot配置
SPRING_OPTS="--spring.profiles.active=prod"

cd $APP_DIR

# 启动应用
nohup java $JVM_OPTS -jar $APP_JAR $SPRING_OPTS > ${LOG_DIR}/backend.log 2>&1 &

echo $! > $PID_FILE
echo "应用已启动，PID: $(cat $PID_FILE)"
