package config

import scala.concurrent.duration._
import org.apache.log4j.Level
import java.sql.Timestamp
import org.apache.log4j.LogManager
import scala.collection.mutable.ArrayBuffer

object  Config {

    //Logger Settings
    val DEBUG_MSG = "Find3Streaming: "
    val DEBUG_MSG_AVG = "Rolling-Avg-" + DEBUG_MSG
    val DEBUG_MSG_DIS = "Rolling-DIS-" + DEBUG_MSG

    val LOG_LEVEL = Level.WARN
    val log = LogManager.getRootLogger
    val DEBUG = true

    //Spark timewindows
    val WATERMARK_SIZE = 5.seconds
    val WINDOW_SIZE = 10.seconds
    val SLIDE_SZIZE = 10.seconds

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

    //AggDistance
    case class OdomPoint(val secs: Long, val nsecs: Long, val x: Double, val y: Double, val z: Double) extends Ordered[OdomPoint] {
        // return 0 if the same, negative if this < that, positive if this > that
        override def compare(that: OdomPoint): Int = {
            if(this.secs < that.secs) { return -1 } 
            else if(this.secs > that.secs) { return +1 } 
            else {
                if(this.nsecs < that.nsecs) { return -1 }
                else if(this.nsecs > that.nsecs) { return +1 }
                else { return 0}
            }
        }
    }
    
    case class Position(val x: Double, val y: Double, val z: Double)
    case class BufferPoints(var points: ArrayBuffer[OdomPoint])    

    //AggDistanceLocal
    val AGGL_BUFFER_SIZE = 1
    
    case class BufferPointsLocal(val distance: Double, val points: ArrayBuffer[OdomPoint])

    //AggRollingAvg
    case class AvgWifiData(var timestamp: Timestamp, var wifiAvg: Double, var runingAverage: Double)
    case class WifiData(var timestamp: Timestamp, var wifiAvg: Double)

    case class Average(var list: List[(Timestamp, Entry)])
    case class Entry(var wifiDB: Double, var sum: Double, var count: Int)

    //Sample json String
    val JSON_SAMPLE = "{\"pose\":{\"position\":{\"x\":0.5428311228752136,\"y\":0.01632818765938282,\"z\":0.0},\"orientation\":{\"x\":0.0083421990275383,\"y\":0.004321090877056122,\"z\":0.03856131061911583,\"w\":0.9992120862007141}},\"header\":{\"seq\":85502,\"stamp\":{\"secs\":1616158742,\"nsecs\":440000000},\"frame_id\":\"base_link\"}}"

}