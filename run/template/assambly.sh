#!/bin/bash
echo "Load Config"
ENV_FILE=./template.env

DOCKER_COMPOSE=../docker-compose-sbt.yml
DOCKERFILE=../.

. ./../assambly.sh
