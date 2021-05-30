echo Submit $JAR_NAME to Spark
echo Found HDFS datanode: $HDFS_CONTAINER
echo Found SPARK master: $SPARK_CONTAINER
echo
#Save jars to HDFS
#Local   container only
echo Copy jar file to HDFS datanode
docker cp $FAT_JAR_PATH $HDFS_CONTAINER:tmp/
echo

echo Create jar folder
docker exec $HDFS_CONTAINER hdfs dfs -mkdir -p $HDFS_PATH
echo

echo Copy jar file from local file system into HDFS
docker exec $HDFS_CONTAINER hdfs dfs -put -f $HDFS_JAR $HDFS_PATH
docker exec $HDFS_CONTAINER hadoop fs -chown -R haw:hadoop $HDFS_USER
echo

echo Submit jar file to Spark
# Run on a Spark standalone cluster in cluster deploy mode
docker exec $SPARK_CONTAINER /opt/bitnami/spark/bin/spark-submit \
    --class $SPARK_CLASS \
    --master spark://master:7077 \
    --deploy-mode cluster \
    --executor-memory 8G \
    --executor-cores $EXECUTER_CORES \
    --total-executor-cores $TOTAL_EXECUTER_CORES \
    $SPARK_PATH
