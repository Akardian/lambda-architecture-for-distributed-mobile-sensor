docker-compose -f kafka/docker-compose-kafka.yml up -d

docker-compose -f hadoop/docker-compose-hadoop.yml up -d

docker-compose -f spark/docker-compose-spark.yml up -d

docker-compose -f druid/docker-compose-druid.yml up -d