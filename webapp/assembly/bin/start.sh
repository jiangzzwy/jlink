#!/bin/sh
JAVA_BIN=$(which java)
if ! [ -x "$(command -v $JAVA_BIN)" ]; then
  echo "No JAVA command found. Please validate environment variable points to valid JDK installation."
  exit 1
fi
JAVA_VERSION=$($JAVA_BIN -version 2>&1 >/dev/null | grep 'java version' | sed s/\"/''/g | awk '{print $3}')
if [[ "$JAVA_VERSION" < "1.7" ]]; then
  echo "Ops, Java version too low."
  exit 1
fi
MAIN_CLASS=com.jlink.JLinkApplication

SERVER_NAME=$(echo $MAIN_CLASS | awk '{split($0,a,".");print a[length(a)]}')
PID_EXIST=$(ps -ef | grep $MAIN_CLASS | awk '!/grep/{print $2}')
if [ ! $PID_EXIST ]; then

  SCRIPT_LOCATION=$0
  BIN_PATH=$(cd $(dirname $SCRIPT_LOCATION) && pwd)
  CONF_PATH=$(dirname $BIN_PATH)/conf
  LIB_PATH=$(dirname $BIN_PATH)/lib
  LOG_PATH=$(dirname $BIN_PATH)/logs

  CLASSPATH=$CLASSPATH:$CONF_PATH:$LIB_PATH

  for i in "${LIB_PATH}/"*.jar; do
    CLASSPATH="${CLASSPATH}:$i"
  done
  for i in "${LIB_PATH}/dependencies/"*.jar; do
    CLASSPATH="${CLASSPATH}:$i"
  done

  JVM_OPTS="-XX:+UseG1GC \
            -Denv:DEV \
            -XX:MaxGCPauseMillis=1000 \
            -XX:InitiatingHeapOccupancyPercent=45 \
            -XX:NewRatio=2 \
            -XX:SurvivorRatio=8 \
            -XX:MaxTenuringThreshold=15 \
            -XX:ParallelGCThreads=4 \
            -XX:ConcGCThreads=4 \
            -XX:G1ReservePercent=10 \
            -XX:+PrintTenuringDistribution \
            -XX:+HeapDumpOnOutOfMemoryError \
            -XX:HeapDumpPath=$LOG_PATH/dumps/$SERVER_NAME.hprof \
            -Xloggc:$LOG_PATH/$SERVER_NAME-gc-worker.log \
            -verbose:gc \
            -XX:+UseGCLogFileRotation \
            -XX:NumberOfGCLogFiles=10 \
            -XX:GCLogFileSize=1m \
            -XX:+PrintGCDetails \
            -XX:+PrintHeapAtGC \
            -XX:+PrintGCTimeStamps -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=22024"

  for i in "${LOG_PATH}/"*"$(date  -d "yesterday" +%Y-%m-%d)".log; do
      echo $i
      rm -rf $i
      sleep 1s
   done

  $JAVA_BIN $JVM_OPTS -DLOG_PATH=$LOG_PATH -cp $CLASSPATH $MAIN_CLASS >/dev/null 2>&1 &
  echo -n "服务 $SERVER_NAME 正在启动  "
fi