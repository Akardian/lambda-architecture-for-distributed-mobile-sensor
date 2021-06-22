HDFS_CONTAINER=$(docker ps -q -n 1 -f name=hdfs_datanode*)
SPARK_CONTAINER=$(docker ps -q -n 1 -f name=spark_master*)

# Application name
NAME=SparkFind3Batch

# Path and Name JAR for submitting
JAR_NAME=sparkFind3Batch-0.2.jar
SPARK_CLASS=SparkFind3Batch
SPARK_PATH=hdfs://namenode:9000/user/haw/spark-jars/$JAR_NAME
FAT_JAR_PATH=../../src/spark/sparkFind3Batch/target/scala-2.12/$JAR_NAME

HDFS_PATH=../haw/spark-jars
HDFS_JAR=tmp/$JAR_NAME
HDFS_USER=/user/haw

# Variable for Batch Applications
IS_STREAMING=false
# Source and Destination path for new Data
LOAD_DATA_FROM=find3Streaming-test

HDFS_PATH_NEW="../haw/find3Streaming-test/new-data/*"
HDFS_PATH_TMP=../haw/find3Streaming-test/tmp-data

PATH_OUTPUT=../sparkExperimental/output

# Core Configuration
EXECUTER_CORES=4
TOTAL_EXECUTER_CORES=4

MEMORY=16G

echo Load $NAME config

. ./../submit.sh
