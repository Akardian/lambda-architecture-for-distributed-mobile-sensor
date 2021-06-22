echo 
echo Submit $JAR_NAME to Spark
echo Found HDFS datanode: $HDFS_CONTAINER
echo Found SPARK master: $SPARK_CONTAINER
echo
#Save jars to HDFS
#Local   container only
echo Copy jar file to HDFS datanode
docker cp $FAT_JAR_PATH $HDFS_CONTAINER:tmp/
echo

echo Create jar folder in [$HDFS_PATH]
docker exec $HDFS_CONTAINER hdfs dfs -mkdir -p $HDFS_PATH
echo

echo Copy jar file from local file system into HDFS
docker exec $HDFS_CONTAINER hdfs dfs -put -f $HDFS_JAR $HDFS_PATH
docker exec $HDFS_CONTAINER hadoop fs -chown -R haw:hadoop $HDFS_USER
echo

echo Check for Streaming Application [IS_STREAMING = $IS_STREAMING]
if [ $IS_STREAMING = true ]
then
    echo Streaming Application
else
    echo Is a batch Application
     driverid=$(cat $PATH_OUTPUT | grep -Po driver-[0-9]+-[0-9]+ | head -1)
     echo Submission ID is [$driverid]
    echo     
     echo Kill $driverid
     docker exec $SPARK_CONTAINER ./bin/spark-submit --kill --master spark://master:6066 $driverid
    
    echo Moving data to temporary folder
    docker exec $HDFS_CONTAINER hdfs dfs -mkdir -p $HDFS_PATH_NEW
    docker exec $HDFS_CONTAINER hdfs dfs -mkdir -p $HDFS_PATH_TMP

    # docker exec $HDFS_CONTAINER hadoop fs -rm  HDFS_PATH_NEW/_spark_metadata
    # docker exec $HDFS_CONTAINER hadoop fs -mv  HDFS_PATH_NEW HDFS_PATH_TMP
fi
echo

echo Submit jar file to Spark
# Run on a Spark standalone cluster in cluster deploy mode
docker exec $SPARK_CONTAINER /opt/bitnami/spark/bin/spark-submit \
    --class $SPARK_CLASS \
    --master spark://master:7077 \
    --deploy-mode cluster \
    --executor-memory $MEMORY \
    --executor-cores $EXECUTER_CORES \
    --total-executor-cores $TOTAL_EXECUTER_CORES \
    $SPARK_PATH $NAME \
    > output 2>&1
    
cat output