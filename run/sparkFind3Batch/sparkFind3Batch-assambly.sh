#!/bin/bash
echo "Load Config"
ENV_FILE=./sparkFind3Batch.env

DOCKER_COMPOSE=../docker-compose-sbt.yml
DOCKERFILE=../.

. ./../assambly.sh
