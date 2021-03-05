import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.{Encoder, Encoders, SparkSession}
import java.sql.Timestamp

import  config.Config._

object  MyRollingAvg extends Aggregator[WifiData, Average, Double] {

    //Initial value of the intermediate results
    def zero: Average = {
        log.warn(DEBUG_MSG_AVG + "##### zero #####")
        Average(entryMap = Map[Timestamp, Entry]())
    }

    //aggegrate input value "wifiData" into current intermediate value "buffer"
    def reduce(buffer: Average, wifiData: WifiData): Average = {
        log.warn(DEBUG_MSG_AVG + "##### reduce #####")
        log.warn(DEBUG_MSG_AVG + "WifiData: " + wifiData.toString())
        log.warn(DEBUG_MSG_AVG + "Buffer: [" + buffer.entryMap.size + "]")
        
        //Count and Sum all already existing entrys

        var sum = wifiData.wifiAvg
        var count = 1
        var size = 1
        buffer.entryMap.foreach{ case (key,value) => 
            if(key.getTime() < wifiData.timestamp.getTime()) {
                sum += value.sum
                count += value.count
                size += 1
            }
        }
    
        log.warn(DEBUG_MSG_AVG + "Sum[" + sum + "] Count[" + count + "]")

        buffer.entryMap += (wifiData.timestamp -> Entry(wifiData.wifiAvg, sum, count))

        log.warn(DEBUG_MSG_AVG + "Out Buffer:" + buffer.toString())
        buffer
    }

    def mapRollingSum(map1: Map[Timestamp, Entry], map2: Map[Timestamp, Entry]): Map[Timestamp, Entry] = {
        map2.foreach{ case (key2, value2) =>
            map1.map{ case (key1, value1) =>
                if(key1.getTime() > key2.getTime()) {
                    value1.sum += value2.wifiDB
                    value1.count += 1
                }
                log.warn(DEBUG_MSG_AVG + "SUM[" + value1.sum + "] COUNT[" + value1.count + "]")        
            }
        }
        map1
    }

    //Merge two intermediate value
    def merge(buffer1: Average, buffer2: Average): Average = {
        log.warn(DEBUG_MSG_AVG + "##### merge #####")
        log.warn(DEBUG_MSG_AVG + "Size: B1[" + buffer1.entryMap.size + "] B2[" + buffer2.entryMap.size + "]")
        
        //Add Both maps to each and Sum values
        val newMap = mapRollingSum(buffer2.entryMap, buffer1.entryMap) ++ mapRollingSum(buffer1.entryMap, buffer2.entryMap)
        
        log.warn(DEBUG_MSG_AVG + "SumMap:" + newMap.values.toString())
        val out = Average(newMap)
        out
    }

    //Transforms the output of the reduction
    def finish(reduction: Average): Double = {
        log.warn(DEBUG_MSG_AVG + "##### finish #####")
        val out = reduction.entryMap.last._2.sum
        out
    }

    def bufferEncoder: Encoder[Average] = Encoders.product
    def outputEncoder: Encoder[Double] = Encoders.scalaDouble
}
