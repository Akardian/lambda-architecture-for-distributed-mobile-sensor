# Application Name and Jar Name
NAME=find3Streaming
JAR_NAME=SparkFindStreaming-0.1.jar

# The Path where your fat jar is located
FAT_JAR_PATH=../../src/spark/sparkFind3Streaming/target/scala-2.12/$JAR_NAME

# HDFS Locations and user
HDFS_PATH=../haw/spark-jars
HDFS_JAR=tmp/$JAR_NAME
HDFS_USER=/user/haw

# Output location/name for the log
PATH_OUTPUT=output

# Is this a Streaming application or a Batch application
IS_STREAMING=true

# Batch Application Settings
# Path to read from tmp folder to new new folder
LOAD_DATA_FROM=find3Streaming-test

HDFS_PATH_NEW=../haw/$LOAD_DATA_FROM/new-data
HDFS_PATH_TMP=../haw/$LOAD_DATA_FROM/tmp-data

# Spark Settings
# Spark Name and hdfs path to jar
SPARK_CLASS=SparkFindStreaming
SPARK_PATH=hdfs://namenode:9000/user/haw/spark-jars/$JAR_NAME

# CPU Core settings
EXECUTER_CORES=3
TOTAL_EXECUTER_CORES=3

MEMORY=16G

. ./../submit.sh
