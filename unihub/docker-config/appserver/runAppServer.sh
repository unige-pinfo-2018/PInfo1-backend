#!/bin/bash

docker network inspect unihub-network 1>/dev/null 2>/dev/null
if [ $? -ne 0 ]; then
     docker network create --subnet 172.0.0.0/16   unihub-network
fi

if [[ "$(docker images -q unihub-wildfly 2> /dev/null)" == "" ]]; then
 	docker build -t unihub-wildfly .
fi
docker run --ip="172.0.0.2" --net="unihub-network" -d  -p 18080:8080 -p 19990:9990 -p 18787:8787 -v "/opt/docker-deploy:/opt/jboss/wildfly/standalone/deployments/:rw" unihub-wildfly
