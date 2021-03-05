package config

import scala.concurrent.duration._
import org.apache.log4j.Level

object Config {
    val NAME = "find3"
    //Logger Settings
    val DEBUG_MSG = "Find3: "
    val LOG_LEVEL = Level.WARN

    //Kafka Settings
    val GROUP_ID = "Spark-Find3"
    val CONTEXT_NAME = NAME + "-consumer"

    val BOOTSTRAP_SERVERS = "kafka01:9092";
    val TOPICS_INPUT = NAME +"-input"
    val TOPICS_OUTPUT = NAME + "-stream"
    
    //Avro Schema
    val SCHEMA_PATH = "avro/FINDData.avsc"

    //Spark timewindows
    val WATERMARK_SIZE = 5.seconds
    val WINDOW_SIZE = 10.seconds
    val SLIDE_SZIZE = 10.seconds

    //Spark Checkpoints
    val CHECKPOINT_HDFS = "hdfs://namenode:9000/user/haw/" + NAME + "/checkpoint/hdfs"
    val CHECKPOINT_KAFKA = "hdfs://namenode:9000/user/haw/" + NAME + "/checkpoint/kafka"

    //HDFS Settings
    val HDFS_PATH = "hdfs://namenode:9000/user/haw/" + NAME + "/new-data"

    //Schama/column names
    val N_TIMESTAMP_KAFKA_IN = "timestampKafkaIn"
    
    val N_TIMESTAMP_FIND_EPOCH = "timestampFindEpoch"
    val N_TIMESTAMP_FIND = "timestampFind"

    val N_SENDERNAME = "senderName"
    val N_LOCATION = "location"
    val N_ODEM_DATA = "odomData"
    val N_WIFI = "wifiData"
    val N_AVG_WIFI = "wifiAvg"
    val N_SUM_TOTAL = "wifiSum"
}
