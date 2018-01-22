#!/usr/bin/env bash

nc -z 127.0.0.1 13306
if [ $? -eq 0 ]; then
    echo "mysql running on 13306"
else
    docker run --rm -d -p13306:3306 412335208158.dkr.ecr.us-east-1.amazonaws.com/mysql-maindb
fi

nc -z 127.0.0.1 23306
if [ $? -eq 0 ]; then
    echo "mysql running on 23306"
else
    docker run --rm -d -p23306:3306 412335208158.dkr.ecr.us-east-1.amazonaws.com/mysql-maindb
fi

java \
    -Xmx1024m \
    -Dcom.sun.management.jmxremote \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -Dcom.sun.management.jmxremote.port=9000 \
    -Djava.rmi.server.hostname=192.168.5.2 \
    -cp target/java-issue-demos.jar com.github.stantonk.java_issue_demos.OrderDeadlock
#    -cp target/java-issue-demos.jar com.github.stantonk.java_issue_demos.Memory
#    -cp target/java-issue-demos.jar com.github.stantonk.java_issue_demos.ResourceExhaustionDeadlock
