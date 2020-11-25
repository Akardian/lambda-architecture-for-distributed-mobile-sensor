docker-compose --env-file ./.env -f kafka/docker-compose-kafka.yml up -d

docker-compose --env-file ./.env -f hdfs/docker-compose-hadoop.yml up -d

docker-compose --env-file ./.env -f spark/docker-compose-spark.yml up -d
