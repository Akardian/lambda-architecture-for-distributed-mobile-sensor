#!/bin/bash
echo "Run Docker with"
echo "env file: " $ENV_FILE
echo "docker compose: " $DOCKER_COMPOSE
echo "docker compose: " $DOCKER_COMPOSE

if [ "$1" == "-b" ]; then 
    echo "Building docker Image <build-sbt>"
    docker build -t build-sbt $DOCKERFILE
else 
    echo "Docker image build skiped. Argument -b to build Docker image first"
fi 
mkdir -p ./tmp/.sbt
mkdir -p ./tmp/.m2
mkdir -p ./tmp/.ivy2

docker-compose --env-file $ENV_FILE -f $DOCKER_COMPOSE run sbt-assembly
