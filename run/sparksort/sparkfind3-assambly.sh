#!/bin/bash
echo "Load Config"
ENV_FILE=./sparksort.env

DOCKER_COMPOSE=../docker-compose-sbt.yml
DOCKERFILE=../.

. ./../assambly.sh
