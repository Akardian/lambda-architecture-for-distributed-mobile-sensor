#!/bin/bash
echo "Load Config"
ENV_FILE=./config.env

DOCKER_COMPOSE=../docker-compose-sbt.yml
DOCKERFILE=../.

. ./../assambly.sh
