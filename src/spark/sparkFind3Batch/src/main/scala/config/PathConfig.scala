package config

import scala.concurrent.duration._
import org.apache.log4j.Level
import java.sql.Timestamp
import org.apache.log4j.LogManager
import scala.collection.mutable.ArrayBuffer

case class PathConfig (name: String, hdfsData: String) {
    var NAME = name
    val HDFS_DATA = hdfsData

    //Spark Checkpoints
    val CHECKPOINT_HDFS = "hdfs://namenode:9000/user/haw/" + NAME + "/checkpoint/hdfs"
    val CHECKPOINT_KAFKA = "hdfs://namenode:9000/user/haw/" + NAME + "/checkpoint/kafka"

    //Kafka Settings
    val GROUP_ID = "Spark-" + NAME
    val CONTEXT_NAME = NAME + "-consumer"

    val BOOTSTRAP_SERVERS = "kafka01:9092";
    val TOPICS_INPUT = NAME + "-input"

    //Topics and Checkpoints
    val TOPICS_WIFIDATA = NAME + "-wifiData-output"
    val CHECKPOINT_KAFKA_WIFIDATA = CHECKPOINT_KAFKA + TOPICS_WIFIDATA

    val TOPICS_WIFIANLY = NAME + "-wifiAnly-output"
    val CHECKPOINT_KAFKA_WIFIANLY = CHECKPOINT_KAFKA + TOPICS_WIFIANLY

    val TOPICS_ODOMCLEAN = NAME + "-OdomClean-output"
    val CHECKPOINT_KAFKA_ODOMCLEAN = CHECKPOINT_KAFKA + TOPICS_ODOMCLEAN

    val TOPICS_ODOMDISTANCE = NAME + "-OdomDist-output"
    val CHECKPOINT_KAFKA_ODOMDISTANCE = CHECKPOINT_KAFKA + TOPICS_ODOMDISTANCE  

    
    //HDFS Settings
    val HDFS_PATH_LOAD = "hdfs://namenode:9000/user/haw/" + HDFS_DATA + "/tmp-data"
    val HDFS_PATH_SAVE = "hdfs://namenode:9000/user/haw/" + HDFS_DATA + "/archive-data"

    val HDFS_PATH = "hdfs://namenode:9000/user/haw/" + NAME + "/data"
    val HDFS_PATH_AVG = HDFS_PATH + "/wifiAvg"
    val HDFS_PATH_DATA = HDFS_PATH + "/wiFiData"
    val HDFS_PATH_ODOM = HDFS_PATH + "/odomData"
    val HDFS_PATH_DIST = HDFS_PATH + "/odomDist"

    //Avro Schema
    val SCHEMA_PATH = "avro/FINDData.avsc"
}