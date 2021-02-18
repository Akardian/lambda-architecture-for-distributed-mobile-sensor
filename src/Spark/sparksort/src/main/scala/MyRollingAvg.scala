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
        if(!buffer.map.contains(wifiData.timestamp)){
            val updateEntry = Entry(wifiData.wifiAvg, 1)
            buffer.map += (wifiData.timestamp -> updateEntry)
            buffer.size += 1     
        }

        var newMap = Map[Timestamp, Entry]()
        //Add wifiData to all already existing timestamps with smaller timestamp
        buffer.map.foreach( bufferMap => 
            if(bufferMap._1.getTime() > wifiData.timestamp.getTime()) { //Not new timestamp
                newMap += (bufferMap._1 -> Entry( //Update Entry in map
                    (bufferMap._2.sum + wifiData.wifiAvg),
                    (bufferMap._2.count + 1)
                ))
            } else {
                newMap += bufferMap //Add unchanged entry to map
            }
        )
        //Update map in buffer
        buffer.map = newMap
        buffer
    }

    //Merge two intermediate value
    override def merge(buffer1: Average, buffer2: Average): Average = {
        var newMap = buffer1.map

        buffer2.map.foreach(bufferMap => 
            if(buffer1.map.contains(bufferMap._1)) {
                val updateEntry = Entry(
                    buffer1.map(bufferMap._1).sum + bufferMap._2.sum, 
                    buffer1.map(bufferMap._1).count + bufferMap._2.count)
                newMap += (bufferMap._1 -> updateEntry)      
            } else {
                val updateEntry = Entry(
                    bufferMap._2.sum, 
                    bufferMap._2.count)
                newMap += (bufferMap._1 -> updateEntry)      
            }
        )
        buffer1.map = newMap
        buffer1
    }

    //Transforms the output of the reduction
    def finish(reduction: Average): Average = {
        reduction
    }

    def bufferEncoder: Encoder[Average] = Encoders.product
    def outputEncoder: Encoder[Average] = Encoders.product
}
