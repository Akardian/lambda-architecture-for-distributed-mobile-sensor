echo "Load Sparkfind3 config"
HDFS_CONTAINER=$(docker ps -q -n 1 -f name=hdfs_datanode*)
SPARK_CONTAINER=$(docker ps -q -n 1 -f name=spark_master*)

JAR_NAME=SparkFind3-0.2.jar

FAT_JAR_PATH=../../src/spark/sparkFind3/target/scala-2.12/$JAR_NAME

HDFS_PATH=../haw/spark-jars
HDFS_JAR=tmp/$JAR_NAME
HDFS_USER=/user/haw

SPARK_CLASS=SparkFind3
SPARK_PATH=hdfs://namenode:9000/user/haw/spark-jars/$JAR_NAME

. ./../submit.sh
