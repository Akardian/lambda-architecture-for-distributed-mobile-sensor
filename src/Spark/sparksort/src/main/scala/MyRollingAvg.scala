import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.{Encoder, Encoders, SparkSession}
import java.sql.Timestamp

import  config.Config._

object  MyRollingAvg extends Aggregator[WifiData, Average, Average] {

    //Initial value of the intermediate results
    def zero: Average = Average(size = 0, map = Map[Timestamp, Entry]())

    //aggegrate input value "wifiData" into current intermediate value "buffer"
    def reduce(buffer: Average, wifiData: WifiData): Average = {
        //Add new entry to map
        //if(!buffer.map.contains(wifiData.timestamp)){
            //Count and Sum all already existing entrys
            var sum = wifiData.wifiAvg
            var count = 1
            buffer.map.foreach{ case (key,value) => 
                   
                    sum += value.sum
                    count += value.count
                    log.debug(DEBUG_MSG_AVG + "Sum: " + sum + " Count: " + count)
            }
            //Add new entry to buffer
        val out = Average(buffer.size + 1, buffer.map + (wifiData.timestamp -> Entry(sum, count)))
        out
    }

    //Merge two intermediate value
    override def merge(buffer1: Average, buffer2: Average): Average = {
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
        log.debug(DEBUG_MSG_AVG + "Finish ")
        reduction
    }

    def bufferEncoder: Encoder[Average] = Encoders.product
    def outputEncoder: Encoder[Average] = Encoders.product
}
