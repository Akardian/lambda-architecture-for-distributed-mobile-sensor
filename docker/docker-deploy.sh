docker stack deploy --compose-file docker-compose-portainer.yml portainer

docker stack deploy --compose-file docker-compose-kafka.yml lambda

docker stack deploy --compose-file docker-compose-hadoop.yml lambda

docker stack deploy --compose-file docker-compose-spark.yml lambda
