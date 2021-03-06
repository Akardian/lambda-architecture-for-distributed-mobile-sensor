docker-compose --env-file ./.env -f kafka/docker-compose-kafka.yml up -d
docker-compose --env-file ./.env -f hdfs/docker-compose-hadoop.yml up -d

docker-compose --env-file ./.env -f spark/docker-compose-spark.yml up -d

HDFS_CONTAINER=$(docker ps -q -n 1 -f name=hdfs_datanode*)

echo Create log directories on $HDFS_CONTAINER
docker exec $HDFS_CONTAINER hdfs dfs -mkdir -p ../haw/shared/event-logs
docker exec $HDFS_CONTAINER hdfs dfs -mkdir -p ../haw/shared/event-logs

echo Starting History server
SPARK_CONTAINER=$(docker ps -q -n 1 -f name=spark_master*)
docker exec -it spark_master_1 sbin/start-history-server.sh

docker-compose --env-file ./.env -f druid/docker-compose-druid.yml up -d
docker-compose --env-file ./.env -f dashboard/docker-compose-grafana.yml up -d
