import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.{Encoder, Encoders, SparkSession}
import java.sql.Timestamp

import  config.Config._

object  MyRollingAvg extends Aggregator[WifiData, Average, Average] {

    //Initial value of the intermediate results
    def zero: Average = Average(size = 0, entryMap = Map[Timestamp, Entry]())

    //aggegrate input value "wifiData" into current intermediate value "buffer"
    def reduce(buffer: Average, wifiData: WifiData): Average = {
        log.warn(DEBUG_MSG_AVG + "reduce")
        log.warn(DEBUG_MSG_AVG + "WifiData:" + wifiData.toString())
        log.warn(DEBUG_MSG_AVG + "Buffer:" + buffer.toString())
        
        //Count and Sum all already existing entrys
        var sum = wifiData.wifiAvg
        var count = 1
        buffer.entryMap.foreach{ case (key,value) => 
            if(key.getTime() < wifiData.timestamp.getTime()) {
                sum += value.sum
                count += value.count
                log.warn(DEBUG_MSG_AVG + "Sum: " + sum + " Count: " + count)
            }
        }
    
        log.warn(DEBUG_MSG_AVG + "Sum:" + sum)
        log.warn(DEBUG_MSG_AVG + "Count:" + count)
            //Add new entry to buffer
        val out = Average(buffer.size + 1, buffer.entryMap + (wifiData.timestamp -> Entry(sum, count)))
        log.warn(DEBUG_MSG_AVG + "Out Buffer:" + out.toString())
        out
    }

    //Merge two intermediate value
    def merge(buffer1: Average, buffer2: Average): Average = {
        log.warn(DEBUG_MSG_AVG + "##### merge #####")
        log.warn(DEBUG_MSG_AVG + "Size: B1[" + buffer1.entryMap.size + "] B2[" + buffer2.entryMap.size + "]")
        /*
        var mergedMap = buffer1.entryMap ++ buffer2.entryMap.map{
            case (key,value) => 
            key -> (Entry(
                value.sum + buffer1.entryMap.getOrElse[Entry](key,Entry(0, 0)).sum, 
                value.count + buffer1.entryMap.getOrElse[Entry](key,Entry(0, 0)).count
                )
            )
        }
        //log.warn(DEBUG_MSG_AVG + "MergedMap:" + mergedMap.toString())
        
        val sumMap = mergedMap.map{ case (key,value) =>
            log.warn(DEBUG_MSG_AVG + "CurrentEntry:" + key + ", " + value)

            var sum = value.sum
            var count = value.count
            mergedMap.foreach{ case (timestamp, entry) =>
                if(timestamp.getTime() < key.getTime()) {
                    sum += entry.sum
                    count += entry.count
                }
            }
            log.warn(DEBUG_MSG_AVG + "Sum:" + sum)
            log.warn(DEBUG_MSG_AVG + "Count:" + count)

            (key, Entry(sum, count))
        }*/

        var sumMap = buffer1.entryMap
        buffer2.entryMap.foreach{ case(key, value) =>
            log.warn(DEBUG_MSG_AVG + "CurrentEntry:" + key + ", " + value)

            if(sumMap.contains(key)) {
                sumMap += (key -> Entry(sumMap(key).sum + value.sum, sumMap(key).count + value.count)) 
            }else {
                sumMap += (key -> value)
            }
            log.warn(DEBUG_MSG_AVG + "Sum:" + sumMap(key).sum)
            log.warn(DEBUG_MSG_AVG + "Count:" + sumMap(key).count)
        }

        log.warn(DEBUG_MSG_AVG + "SumMap:" + sumMap.toString())
        val out = Average(buffer1.size + buffer2.size, sumMap)
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
