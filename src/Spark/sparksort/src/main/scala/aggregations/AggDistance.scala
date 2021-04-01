package aggregations

import org.apache.spark.sql.{Encoder, Encoders, SparkSession}
import org.apache.spark.sql.expressions.Aggregator

import  config.Config._
import scala.collection.immutable.SortedSet
import scala.math.pow
import scala.math.sqrt

object  AggDistance extends Aggregator[OdomPoint, BufferPoints, Double] {

    //Initial value of the intermediate results
    def zero: BufferPoints = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance zero #####")
        
        val buffer = BufferPoints(SortedSet.empty[OdomPoint](TimeOrdering))

        log.warn(DEBUG_MSG_AVG + buffer)
        buffer
    }

    //aggegrate input value "wifiData" into current intermediate value "buffer"
    def reduce(buffer: BufferPoints, odom: OdomPoint): BufferPoints = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance reduce #####")
        
        buffer.points += odom
        val out = BufferPoints(buffer.points + odom)

        log.warn(DEBUG_MSG_AVG + out)
        out
    }

    //Merge two intermediate value
    def merge(buffer1: BufferPoints, buffer2: BufferPoints): BufferPoints = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance merge #####")
        
        val out = BufferPoints(buffer1.points ++ buffer2.points)
        
        log.warn(DEBUG_MSG_AVG + out)
        out
    }

    //Transforms the output of the reduction
    def finish(reduction: BufferPoints): Double = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance finish #####")

        var sum = reduction.points.foldLeft((0.0, Position(Double.NaN, Double.NaN, Double.NaN))) { (z, elem) => 

            var next = (0.0, Position(elem.x, elem.y, elem.z))
            if(!z._2.x.isNaN()) {
                val distance = distanceBetween(z._2, Position(elem.x, elem.y, elem.z))
                next = (distance, Position(elem.x, elem.y, elem.z))
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
