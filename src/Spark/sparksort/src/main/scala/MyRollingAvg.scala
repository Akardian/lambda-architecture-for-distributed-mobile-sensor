import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.{Encoder, Encoders, SparkSession}
import java.sql.Timestamp

import  config.Config._

object  MyRollingAvg extends Aggregator[WifiData, Average, Average] {

    //Initial value of the intermediate results
    def zero: Average = Average(size = 0, map = Map[Timestamp, Entry]())

    //aggegrate input value "wifiData" into current intermediate value "buffer"
    def reduce(buffer: Average, wifiData: WifiData): Average = {
        log.warn(DEBUG_MSG_AVG + "reduce")
        log.warn(DEBUG_MSG_AVG + "WifiData:" + wifiData.toString())
        log.warn(DEBUG_MSG_AVG + "Buffer:" + buffer.toString())
        //Add new entry to map
        //if(!buffer.map.contains(wifiData.timestamp)){
            //Count and Sum all already existing entrys
            var sum = wifiData.wifiAvg
            var count = 1
            buffer.map.foreach{ case (key,value) => 
                   
                    sum += value.sum
                    count += value.count
                    log.warn(DEBUG_MSG_AVG + "Sum: " + sum + " Count: " + count)
            }
        log.warn(DEBUG_MSG_AVG + "Sum:" + sum)
        log.warn(DEBUG_MSG_AVG + "Count:" + count)
            //Add new entry to buffer
        val out = Average(buffer.size + 1, buffer.map + (wifiData.timestamp -> Entry(sum, count)))
        log.warn(DEBUG_MSG_AVG + "Out Buffer:" + out.toString())
        out
    }

    //Merge two intermediate value
    override def merge(buffer1: Average, buffer2: Average): Average = {
        log.warn(DEBUG_MSG_AVG + "merge")
        log.warn(DEBUG_MSG_AVG + "Buffer1:" + buffer1.toString())
        log.warn(DEBUG_MSG_AVG + "Buffer2:" + buffer2.toString())
        var newMap = buffer1.map

        buffer2.map.foreach{ case (key,value) => 
            if(newMap.contains(key)) {
                newMap += (key -> Entry(
                    newMap(key).sum + value.sum, 
                    newMap(key).count + value.count
                ))      
            } else {
                newMap += (key -> Entry(value.sum, value.count))      
            }
        }

        val out = Average(buffer1.size + buffer2.size, newMap)
        out
    }

    //Transforms the output of the reduction
    def finish(reduction: Average): Average = {
        log.warn(DEBUG_MSG_AVG + "finish")
        reduction
    }

    def bufferEncoder: Encoder[Average] = Encoders.product
    def outputEncoder: Encoder[Average] = Encoders.product
}
