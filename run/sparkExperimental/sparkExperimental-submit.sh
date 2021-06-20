HDFS_CONTAINER=$(docker ps -q -n 1 -f name=hdfs_datanode*)
SPARK_CONTAINER=$(docker ps -q -n 1 -f name=spark_master*)

NAME=find3-streaming-test
JAR_NAME=SparkFindStreaming-0.1.jar

FAT_JAR_PATH=../../src/spark/sparkExperimental/target/scala-2.12/$JAR_NAME

HDFS_PATH=../haw/spark-jars
HDFS_JAR=tmp/$JAR_NAME
HDFS_USER=/user/haw

HDFS_PATH_NEW=../haw/$NAME/new-data
HDFS_PATH_TMP=../haw/$NAME/tmp-data

IS_STREAMING=true

SPARK_CLASS=SparkFindStreaming
SPARK_PATH=hdfs://namenode:9000/user/haw/spark-jars/$JAR_NAME

EXECUTER_CORES=3
TOTAL_EXECUTER_CORES=1

echo Load $NAME config

. ./../submit.sh
