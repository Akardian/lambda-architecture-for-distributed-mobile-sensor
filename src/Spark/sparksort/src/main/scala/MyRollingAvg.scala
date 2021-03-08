import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.{Encoder, Encoders, SparkSession}
import java.sql.Timestamp

import  config.Config._

object  MyRollingAvg extends Aggregator[WifiData, Average, Average] {

    //Initial value of the intermediate results
    def zero: Average = {
        log.warn(DEBUG_MSG_AVG + "##### zero #####")
        Average(list = List[(Timestamp, Entry)]())
    }

    //aggegrate input value "wifiData" into current intermediate value "buffer"
    def reduce(buffer: Average, wifiData: WifiData): Average = {
        log.warn(DEBUG_MSG_AVG + "##### reduce #####")
        log.warn(DEBUG_MSG_AVG + "WifiData: " + wifiData.toString())
        log.warn(DEBUG_MSG_AVG + "Buffer: [" + buffer.list.length + "]")
        
        //Create new Entry
        val newEntry = Average(List((wifiData.timestamp, Entry(wifiData.wifiAvg, wifiData.wifiAvg, 1))))
        log.warn(DEBUG_MSG_AVG + "newEntry: [" + newEntry.list.length + "]")

        //Add Buffer + NewEntry and NewEntry + Buffer
        val newMap = mapRollingSum(newEntry.list, buffer.list) ++ mapRollingSum(buffer.list, newEntry.list)

        log.warn(DEBUG_MSG_AVG + "Out: [" + newMap.length + "]")
        val out = Average(newMap)
        out
    }

    def mapRollingSum(list1: List[(Timestamp, Entry)], list2: List[(Timestamp, Entry)]): List[(Timestamp, Entry)] = {
        list2.foreach{ case (key2, value2) =>
            list1.map{ case (key1, value1) =>
                if(key1.getTime() >= key2.getTime()) {
                    value1.sum += value2.wifiDB
                    value1.count += 1
                }
                log.warn(DEBUG_MSG_AVG + "SUM[" + value1.sum + "] COUNT[" + value1.count + "]")        
            }
        }
        list1
    }

    //Merge two intermediate value
    def merge(buffer1: Average, buffer2: Average): Average = {
        log.warn(DEBUG_MSG_AVG + "##### merge #####")
        log.warn(DEBUG_MSG_AVG + "Size: B1[" + buffer1.list.length + "] B2[" + buffer2.list.length + "]")
        
        //Add Both maps to each and Sum values
        val newMap = mapRollingSum(buffer2.list, buffer1.list) ++ mapRollingSum(buffer1.list, buffer2.list)
        
        log.warn(DEBUG_MSG_AVG + "Out: [" + newMap.length + "]")
        val out = Average(newMap)
        out
    }

    //Transforms the output of the reduction
    def finish(reduction: Average): Average = {
        log.warn(DEBUG_MSG_AVG + "##### finish #####")
        reduction
    }

    def bufferEncoder: Encoder[Average] = Encoders.product
    def outputEncoder: Encoder[Average] = Encoders.product
}
