#!/bin/bash
echo "Load Config"
ENV_FILE=./sparkfind3.env

DOCKER_COMPOSE=../docker-compose-sbt.yml
DOCKERFILE=../.

. ./../assambly.sh
