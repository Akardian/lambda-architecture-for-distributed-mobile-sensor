package config

import scala.concurrent.duration._
import org.apache.log4j.Level
import java.sql.Timestamp
import org.apache.log4j.LogManager
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SortedSet

object Config {
    val NAME = "find3Generator"
    //Logger Settings
    val DEBUG_MSG = "Sort: "
    val DEBUG_MSG_AVG = "Rolling-Avg-" + DEBUG_MSG
    val DEBUG_MSG_DIS = "Rolling-DIS-" + DEBUG_MSG
    val LOG_LEVEL = Level.WARN
    val log = LogManager.getRootLogger
    val DEBUG = true

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

    //AggPoint
    case class OdomPoint(val senderName: String, val secs: Long, val nsecs: Long, val x: Double, val y: Double, val z: Double)
    case class Position(val x: Double, val y: Double, val z: Double)
    case class BufferPoints(var points: SortedSet[OdomPoint])

    case class BufferPointsLocal(var distance: Double, var position: Position)

    object TimeOrdering extends Ordering[OdomPoint] {
        def compare(element1:OdomPoint, element2:OdomPoint): Int = {
            if(element1.secs < element2.secs) { return -1 } 
            else if(element1.secs > element2.secs) { return +1 } 
            else {
                if(element1.nsecs < element2.nsecs) { return -1 }
                else if(element1.nsecs > element2.nsecs) { return +1 }
                else { return 0}
            }
        }
    }

    //AggRollingAvg
    case class AvgWifiData(var timestamp: Timestamp, var wifiAvg: Double, var runingAverage: Double)
    case class WifiData(var timestamp: Timestamp, var wifiAvg: Double)

    case class Average(var list: List[(Timestamp, Entry)])
    case class Entry(var wifiDB: Double, var sum: Double, var count: Int)

    //Sample json String
    val JSON_SAMPLE = "{\"pose\":{\"position\":{\"x\":0.5428311228752136,\"y\":0.01632818765938282,\"z\":0.0},\"orientation\":{\"x\":0.0083421990275383,\"y\":0.004321090877056122,\"z\":0.03856131061911583,\"w\":0.9992120862007141}},\"header\":{\"seq\":85502,\"stamp\":{\"secs\":1616158742,\"nsecs\":440000000},\"frame_id\":\"base_link\"}}"

}
