package config

import scala.concurrent.duration._
import org.apache.log4j.Level
import java.sql.Timestamp
import org.apache.log4j.LogManager

object Config {
    val NAME = "find3Generator"
    //Logger Settings
    val DEBUG_MSG = "Sort: "
    val DEBUG_MSG_AVG = "Rolling-Avg-" + DEBUG_MSG
    val LOG_LEVEL = Level.WARN
    val log = LogManager.getRootLogger

    //Kafka Settings
    val GROUP_ID = "Spark-" + NAME
    val CONTEXT_NAME = NAME + "-consumer"

    val BOOTSTRAP_SERVERS = "kafka01:9092";
    val TOPICS_INPUT = NAME + "-input"
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

    val N_TIMESTAMP_FIND_UNIX = "timestampFindUnix"
    val N_TIMESTAMP_FIND = "timestampFind"
    val N_TIMESTAMP_HDFS = "timestampHDFS"

    val N_SENDERNAME = "senderName"
    val N_LOCATION = "location"
    val N_ODEM_DATA = "odomData"
    val N_WIFI = "wifiData"
    val N_AVG_WIFI = "wifiAvg"
    val N_SUM_TOTAL = "wifiSum"

    //Case class section
    case class AvgWifiData(var timestamp: Timestamp, var wifiAvg: Double, var runingAverage: Double)
    case class WifiData(var timestamp: Timestamp, var wifiAvg: Double)

    case class Average(var list: List[(Timestamp, Entry)])
    case class Entry(var wifiDB: Double, var sum: Double, var count: Int)
}
