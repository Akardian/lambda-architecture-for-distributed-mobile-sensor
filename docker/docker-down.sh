docker-compose -f kafka/docker-compose-kafka.yml down

docker-compose -f hadoop/docker-compose-hadoop.yml down

docker-compose -f spark/docker-compose-spark.yml down

docker-compose -f druid/docker-compose-druid.yml down

docker-compose -f dashboard/docker-compose-dasboard.yml down