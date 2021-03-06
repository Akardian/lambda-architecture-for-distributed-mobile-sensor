package aggregations

import org.apache.spark.sql.{Encoder, Encoders, SparkSession}
import org.apache.spark.sql.expressions.Aggregator

import config.Config._
import scala.math.pow
import scala.math.sqrt
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.sql.Row

object  AggDistance extends Aggregator[OdomPoint, BufferPoints, Double] {

    //Initial value of the intermediate results
    def zero: BufferPoints = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance zero #####")
        
        val buffer = BufferPoints(ArrayBuffer[OdomPoint]())

        log.warn(DEBUG_MSG_AVG + "Points[" + buffer.points.length + "]")
        buffer
    }

    //aggegrate input value "wifiData" into current intermediate value "buffer"
    def reduce(buffer: BufferPoints, odom: OdomPoint): BufferPoints = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance reduce #####")

        buffer.points += odom
        buffer.points.sorted

        log.warn(DEBUG_MSG_AVG  + "Points[" + buffer.points.length + "]")
        buffer
    }

    //Merge two intermediate value
    def merge(buffer1: BufferPoints, buffer2: BufferPoints): BufferPoints = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance merge #####")
        
        val out = BufferPoints(buffer1.points ++ buffer2.points)
        out.points.sorted
        
        log.warn(DEBUG_MSG_AVG  + "Points[" + out.points.length + "]")
        out
    }

    //Transforms the output of the reduction
    def finish(reduction: BufferPoints): Double = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance finish #####")

        var sum = reduction.points.foldLeft((0.0, Position(Double.NaN, Double.NaN, Double.NaN))) { (z, elem) => 
            log.warn(DEBUG_MSG_AVG + "Z: " + z)
            log.warn(DEBUG_MSG_AVG + "Elem: " + elem)

            val next = if(z._2.x.isNaN()) {
                log.warn(DEBUG_MSG_AVG + "isNaN: " + z._2)
                (z._1 + 0.0, Position(elem.x, elem.y, elem.z))
            } else {
                val distance = distanceBetween(z._2, Position(elem.x, elem.y, elem.z))
                
                log.warn(DEBUG_MSG_AVG + "Distance: " + distance)
                (z._1 + distance, Position(elem.x, elem.y, elem.z))
            }
            next
        }._1

        log.warn(DEBUG_MSG_AVG + sum)
        sum
    }

    def bufferEncoder: Encoder[BufferPoints] = Encoders.product
    def outputEncoder: Encoder[Double] = Encoders.scalaDouble

    //https://www.calculatorsoup.com/calculators/geometry-solids/distance-two-points.php
    def distanceBetween(p1: Position, p2: Position): Double = {
        sqrt(pow(p2.x - p1.x, 2) + pow(p2.y - p1.y, 2) + pow(p2.z - p1.z, 2))
    }
}
