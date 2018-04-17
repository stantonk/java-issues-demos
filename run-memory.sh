#!/usr/bin/env bash

function ctrl_c {
  echo -e "\nperforming cleanup...\n"
  docker-compose -f docker-compose.Memory.yml stop
  echo -e "\ndone.\n"
}
trap ctrl_c INT

docker-compose -f docker-compose.Memory.yml stop && \
  docker-compose -f docker-compose.Memory.yml rm -f && \
  docker-compose -f docker-compose.Memory.yml up -d
sleep 10

java \
    -Xms128m \
    -Xmx128m \
    -XX:+PrintClassHistogram \
    -Dcom.sun.management.jmxremote \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -Dcom.sun.management.jmxremote.port=9000 \
    -Dbus.config.file_path=./local-bus-config.yaml \
    -cp target/java-issue-demos.jar com.github.stantonk.java_issue_demos.Memory

#    -Djava.rmi.server.hostname=192.168.5.2 \

#    -cp target/java-issue-demos.jar com.github.stantonk.java_issue_demos.OrderDeadlock
#    -cp target/java-issue-demos.jar com.github.stantonk.java_issue_demos.ResourceExhaustionDeadlock
