#!/bin/bash

# Java 경로 설정
JAVA17_HOME=/usr/lib/jvm/java-17-openjdk-amd64
JAVA11_HOME=/usr/lib/jvm/java-11-openjdk-amd64

# HBase 버전 설정
HBASE_VERSION=2.6.1
HBASE_DIR="./hbase-$HBASE_VERSION"

# Pinpoint 버전 설정
PINPOINT_COLLECTOR_JAR="pinpoint-collector-starter-3.0.1-exec.jar"
PINPOINT_WEB_JAR="pinpoint-web-starter-3.0.1-exec.jar"
# PINPOINT_COLLECTOR_JAR="pinpoint-collector-3.0.1-exec.jar"
# PINPOINT_WEB_JAR="pinpoint-web-3.0.1-exec.jar"

# 로그 디렉토리 생성
LOG_DIR="./logs"
mkdir -p $LOG_DIR

# 1. Start HBase (Java 11)
echo "Starting HBase (Version: $HBASE_VERSION) with Java 11..."
# JAVA_HOME=$JAVA11_HOME $HBASE_DIR/bin/start-hbase.sh > $LOG_DIR/hbase-start.log 2>&1
nohup bash -c "export JAVA_HOME=$JAVA11_HOME && $HBASE_DIR/bin/start-hbase.sh" > $LOG_DIR/hbase-start.log 2>&1 &
if [ $? -ne 0 ]; then
  echo "HBase failed to start. Check the log: $LOG_DIR/hbase-start.log"
  exit 1
fi
echo "HBase started successfully."

# HBase 초기화를 위한 대기 시간 추가
echo "Waiting for HBase to initialize..."
sleep 10

# 2. Run HBase shell to create tables (Java 11)
echo "Running HBase shell for table creation with Java 11..."
JAVA_HOME=$JAVA11_HOME $HBASE_DIR/bin/hbase shell hbase-create.hbase > $LOG_DIR/hbase-create.log 2>&1
if [ $? -ne 0 ]; then
  echo "HBase shell execution failed. Check the log: $LOG_DIR/hbase-create.log"
  exit 1
fi
echo "HBase tables created successfully."

# 3. Start Pinpoint Collector (Java 17)
echo "Starting Pinpoint Collector with Java 17..."
echo "Using Java version: $($JAVA17_HOME/bin/java -version 2>&1 | head -n 1)" >> $LOG_DIR/collector.log
nohup $JAVA17_HOME/bin/java -jar -Dpinpoint.zookeeper.address=localhost -Dpinpoint.modules.realtime.enabled=false $PINPOINT_COLLECTOR_JAR > $LOG_DIR/collector.log 2> $LOG_DIR/collector-error.log &
# nohup $JAVA17_HOME/bin/java -jar -Dpinpoint.zookeeper.address=localhost $PINPOINT_COLLECTOR_JAR > $LOG_DIR/collector.log 2> $LOG_DIR/collector-error.log &
if [ $? -ne 0 ]; then
  echo "Pinpoint Collector failed to start. Check the log: $LOG_DIR/collector-error.log"
  exit 1
fi
echo "Pinpoint Collector started successfully."

# 4. Start Pinpoint Web (Java 17)
echo "Starting Pinpoint Web with Java 17..."
echo "Using Java version: $($JAVA17_HOME/bin/java -version 2>&1 | head -n 1)" >> $LOG_DIR/web.log
nohup $JAVA17_HOME/bin/java -jar -Dpinpoint.zookeeper.address=localhost $PINPOINT_WEB_JAR > $LOG_DIR/web.log 2> $LOG_DIR/web-error.log &
if [ $? -ne 0 ]; then
  echo "Pinpoint Web failed to start. Check the log: $LOG_DIR/web-error.log"
  exit 1
fi
echo "Pinpoint Web started successfully."

echo "All services started successfully."
