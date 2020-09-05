docker stack deploy -c kafka/docker-compose-kafka.yml lambda

docker stack deploy -c hadoop/docker-compose-hadoop.yml lambda

docker stack deploy -c spark/docker-compose-spark.yml lambda

docker-compose -f druid/docker-compose-druid.yml up -d