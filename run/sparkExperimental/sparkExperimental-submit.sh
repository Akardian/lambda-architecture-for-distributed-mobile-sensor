echo "Load SparkExperimental config"
HDFS_CONTAINER=$(docker ps -q -n 1 -f name=hdfs_datanode*)
SPARK_CONTAINER=$(docker ps -q -n 1 -f name=spark_master*)

JAR_NAME=SparkExperimental-0.1.jar

FAT_JAR_PATH=../../src/spark/sparkExperimental/target/scala-2.12/$JAR_NAME

HDFS_PATH=../haw/spark-jars
HDFS_JAR=tmp/$JAR_NAME
HDFS_USER=/user/haw

SPARK_CLASS=SparkExperimental
SPARK_PATH=hdfs://namenode:9000/user/haw/spark-jars/$JAR_NAME

EXECUTER_CORES=1

. ./../submit.sh
