#!/bin/bash

docker network inspect unihub-network 1>/dev/null 2>/dev/null
if [ $? -ne 0 ]; then
	docker network create --subnet 172.0.0.0/16   unihub-network
fi


if [[ "$(docker images -q unihub-mysql 2> /dev/null)" == "" ]]; then
 	docker build -t unihub-mysql .
fi
docker run --ip="172.0.0.3" --net="unihub-network" -p 13306:3306 -e MYSQL_ROOT_PASSWORD=admin -d unihub-mysql
