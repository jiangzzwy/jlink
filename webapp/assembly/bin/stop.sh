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
MAIN_CLASS=${mainClass}

SERVER_NAME=$(echo $MAIN_CLASS | awk '{split($0,a,".");print a[length(a)]}')
PID_EXIST=$(ps -ef | grep $MAIN_CLASS | awk '!/grep/{print $2}')
echo
if [ ! $PID_EXIST ]; then
  echo "服务 $SERVER_NAME 已经关闭了无需关闭！"
  echo
  exit 1
else
  COUNT=0
  MAX_WAIT=600
  echo -n "正在尝试关闭 $SERVER_NAME 服务  "
  kill -15 $PID_EXIST >/dev/null 2>&1
  while [ $COUNT -le $MAX_WAIT ]; do
    ((COUNT = COUNT + 1))
    sleep 0.1s
    if [[ $(expr $COUNT % 4) == 1 ]]; then
      echo -ne "\b|"
    elif [[ $(expr $COUNT % 4) == 2 ]]; then
      echo -ne "\b/"
    elif [[ $(expr $COUNT % 4) == 3 ]]; then
      echo -ne "\b-"
    else
      echo -ne "\b"
      echo -n '\'
    fi
    if [ $(ps -ef | grep $MAIN_CLASS | awk '!/grep/{print $2}') ]; then
      if [ $COUNT -ge $MAX_WAIT ]; then
        echo ""
        echo "已经耗时 $(expr $COUNT \* 100) 毫秒，强制杀死进程 PID $(ps -ef | grep $MAIN_CLASS | awk '!/grep/{print $2}')"
        kill -9 $(ps -ef | grep $MAIN_CLASS | awk '!/grep/{print $2}')
        exit 1
      fi
    else
      break
    fi
  done
  echo ""
  echo "服务 $SERVER_NAME PID $PID_EXIST 优雅关闭了,耗时 $(expr $COUNT \* 100) 毫秒"
fi