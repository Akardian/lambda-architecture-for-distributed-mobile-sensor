HDFS_CONTAINER=$(docker ps -q -n 1 -f name=hdfs_datanode*)
SPARK_CONTAINER=$(docker ps -q -n 1 -f name=spark_master*)

NAME=SparkFind3Batch
JAR_NAME=sparkFind3Batch-0.2.jar

FAT_JAR_PATH=../../src/spark/sparkFind3Batch/target/scala-2.12/$JAR_NAME

HDFS_PATH=../haw/spark-jars
HDFS_JAR=tmp/$JAR_NAME
HDFS_USER=/user/haw

HDFS_PATH_NEW="../haw/find3Streaming-test/new-data/timestamp*"
HDFS_PATH_TMP=../haw/find3Streaming-test/tmp-data

PATH_OUTPUT=../sparkExperimental/output

IS_STREAMING=false

SPARK_CLASS=SparkFind3Batch
SPARK_PATH=hdfs://namenode:9000/user/haw/spark-jars/$JAR_NAME

EXECUTER_CORES=4
TOTAL_EXECUTER_CORES=4

MEMORY=16G

echo Load $NAME config

. ./../submit.sh
