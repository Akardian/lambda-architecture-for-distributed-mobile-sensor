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
            var sum = 0.0
            var count = 0
            buffer.map.foreach(bufferMap => 
                if(true) {
                    sum += bufferMap._2.sum
                    count += bufferMap._2.count
                }
            )
            //Add Sum of new Entry to old ones and add +1 to count
            val updateEntry = Entry(sum + wifiData.wifiAvg, count + 1)

            //Add new entry to buffer
            buffer.map += (wifiData.timestamp -> updateEntry)
            buffer.size += 1 //Map Size
        //}
/*
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
        buffer.map = newMap*/
        buffer
    }

    //Merge two intermediate value
    override def merge(buffer1: Average, buffer2: Average): Average = {
        var newMap = buffer1.map

        buffer2.map.foreach(bufferMap => 
            if(newMap.contains(bufferMap._1)) {
                val updateEntry = Entry(
                    (newMap(bufferMap._1).sum + bufferMap._2.sum), 
                    (newMap(bufferMap._1).count + bufferMap._2.count)
                )
                newMap += (bufferMap._1 -> updateEntry)      
            } else {
                val updateEntry = Entry(
                    bufferMap._2.sum, 
                    bufferMap._2.count
                )
                newMap += (bufferMap._1 -> updateEntry)      
            }
        )
        buffer1.map = newMap
        buffer1.size += buffer2.size
        buffer1
    }

    //Transforms the output of the reduction
    def finish(reduction: Average): Average = {
        reduction
    }

    def bufferEncoder: Encoder[Average] = Encoders.product
    def outputEncoder: Encoder[Average] = Encoders.product
}
