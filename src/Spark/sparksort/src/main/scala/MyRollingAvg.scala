import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.{Encoder, Encoders, SparkSession}
import java.sql.Timestamp

import  config.Config._

object  MyRollingAvg extends Aggregator[WifiData, Average, Average] {

    //Initial value of the intermediate results
    def zero: Average = Average(map = Map[Timestamp, Entry]())

    //aggegrate input value "wifiData" into current intermediate value "buffer"
    def reduce(buffer: Average, wifiData: WifiData): Average = {
        //Add wifiData to all already existing timestamps with smaller timestamp
        var newMap = Map[Timestamp, Entry]()
        buffer.map.foreach( elem => 
            if(elem._1.getTime() > wifiData.timestamp.getTime()) {
                newMap += (elem._1 -> Entry( //Update Entry in map
                    elem._2.sum + wifiData.wifiAvg,
                    elem._2.count + 1
                ))
            } else {
                newMap += elem //Add unchanged entry to map
            }
        )

        //Add new entry to map
        if(!buffer.map.contains(wifiData.timestamp)){
            val updateEntry = Entry(wifiData.wifiAvg, 1)
            buffer.map += (wifiData.timestamp -> updateEntry)      
        }
        //Update map in buffer
        buffer.map = newMap
        buffer
    }

    //Merge two intermediate value
    override def merge(buffer1: Average, buffer2: Average): Average = {
        var newMap = buffer1.map

        buffer2.map.foreach(elem => 
            if(buffer1.map.contains(elem._1)) {
                val updateEntry = Entry(
                    buffer1.map(elem._1).sum + elem._2.sum, 
                    buffer1.map(elem._1).count + elem._2.count)
                newMap += (elem._1 -> updateEntry)      
            } else {
                val updateEntry = Entry(
                    elem._2.sum, 
                    elem._2.count)
                newMap += (elem._1 -> updateEntry)      
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
