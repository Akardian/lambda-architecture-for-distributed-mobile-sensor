#!/bin/bash
echo "Load Config"
ENV_FILE=./config.env

DOCKER_COMPOSE=../docker/docker-compose-sbt.yml
DOCKERFILE=../docker/.

. ./../assambly.sh
