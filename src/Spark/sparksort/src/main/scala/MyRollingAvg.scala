import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.{Encoder, Encoders, SparkSession}
import java.security.Timestamp

case class WifiData(avgList: List[Double])
case class Average(var sum: Double, var count: Double)

object  MyRollingAvg extends Aggregator[WifiData, Average, Double] {

    //Initial value of the intermediate results
    def zero: Average = Average(0D, 0D)

    //aggegrate input value "wifiData" into current intermediate value "buffer"
    def reduce(buffer: Average, wifiData: WifiData): Average = {
        wifiData.avgList.foreach { elem =>
            buffer.sum += elem
            buffer.count += 1
        }
        buffer
    }

    //Merge two intermediate value
    override def merge(buffer1: Average, buffer2: Average): Average = {
        buffer1.sum += buffer2.sum
        buffer1.count += buffer2.count
        buffer1
    }

    //Transorfs the output of the reduction
    def finish(reduction: Average): Double = {
        val result = reduction.sum / reduction.count
        result
    }

    def bufferEncoder: Encoder[Average] = Encoders.product
    def outputEncoder: Encoder[Double] = Encoders.scalaDouble
}
