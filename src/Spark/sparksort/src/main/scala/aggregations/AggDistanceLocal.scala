package aggregations

import org.apache.spark.sql.{Encoder, Encoders, SparkSession}
import org.apache.spark.sql.expressions.Aggregator
import java.sql.Timestamp

import  config.Config._
import scala.collection.mutable.SortedSet

import scala.math.pow
import scala.math.sqrt

object  AggDistanceLocal extends Aggregator[OdomPoint, BufferPointsLocal, Double] {

    //Initial value of the intermediate results
    def zero: BufferPointsLocal = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance zero #####")
        
        val buffer = BufferPointsLocal(0.0, Position(Double.NaN, Double.NaN, Double.NaN))

        log.warn(DEBUG_MSG_AVG + buffer)
        buffer
    }

    //aggegrate input value "wifiData" into current intermediate value "buffer"
    def reduce(buffer: BufferPointsLocal, odom: OdomPoint): BufferPointsLocal = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance reduce #####")
             
        val nextPoint = Position(odom.x, odom.y, odom.z)
        val next = 
        if(buffer.position.x.isNaN()) {
            BufferPointsLocal(0.0, nextPoint)
        } else {
            val distance = distanceBetween(buffer.position, nextPoint)
            BufferPointsLocal(distance, nextPoint)
        }

        next
    }

    //Merge two intermediate value
    def merge(buffer1: BufferPointsLocal, buffer2: BufferPointsLocal): BufferPointsLocal = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance merge #####")
        
        val distance =
        if(!buffer1.position.x.isNaN() || !buffer2.position.x.isNaN()) {
            distanceBetween(buffer1.position, buffer2.position)
        } else {
            0.0
        }

        val out = BufferPointsLocal(buffer1.distance + buffer2.distance + distance, buffer1.position)
        
        log.warn(DEBUG_MSG_AVG + out)
        buffer1
    }

    //Transforms the output of the reduction
    def finish(reduction: BufferPointsLocal): Double = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance finish #####")

        var sum = reduction.distance

        log.warn(DEBUG_MSG_AVG + sum)
        sum
    }

    def bufferEncoder: Encoder[BufferPointsLocal] = Encoders.product
    def outputEncoder: Encoder[Double] = Encoders.scalaDouble

    //https://www.calculatorsoup.com/calculators/geometry-solids/distance-two-points.php
    def distanceBetween(p1: Position, p2: Position): Double = {
        sqrt(pow(p2.x - p1.x, 2) + pow(p2.y - p1.y, 2) + pow(p2.z - p1.z, 2))
    }
}
