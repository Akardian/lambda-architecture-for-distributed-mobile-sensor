package aggregations

import org.apache.spark.sql.{Encoder, Encoders, SparkSession}
import org.apache.spark.sql.expressions.Aggregator

import config.Config._
import scala.math.pow
import scala.math.sqrt
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.sql.Row

object  AggDistanceLocal extends Aggregator[OdomPoint, BufferPointsLocal, Double] {

    //Initial value of the intermediate results
    def zero: BufferPointsLocal = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance zero #####")
        
        val buffer = BufferPointsLocal(0, ArrayBuffer[OdomPoint]())

        log.warn(DEBUG_MSG_AVG + "Points[" + buffer.points.length + "]")
        buffer
    }

    //aggegrate input value "wifiData" into current intermediate value "buffer"
    def reduce(buffer: BufferPointsLocal, odom: OdomPoint): BufferPointsLocal = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance reduce #####")

        buffer.points += odom
        buffer.points.sorted

        sumDistanceBetween(buffer, AGGL_BUFFER_SIZE)

        log.warn(DEBUG_MSG_AVG  + "Points[" + buffer.points.length + "]")
        buffer
    }

    //Merge two intermediate value
    def merge(buffer1: BufferPointsLocal, buffer2: BufferPointsLocal): BufferPointsLocal = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance merge #####")
        
        val buffer = BufferPointsLocal(0 ,buffer1.points ++ buffer2.points)
        buffer.points.sorted
        
        sumDistanceBetween(buffer, AGGL_BUFFER_SIZE)

        log.warn(DEBUG_MSG_AVG  + "Points[" + buffer.points.length + "]")
        buffer
    }

    //Transforms the output of the reduction
    def finish(reduction: BufferPointsLocal): Double = {
        log.warn(DEBUG_MSG_AVG + "##### AggDistance finish #####")

        sumDistanceBetween(reduction, 1)
        val sum = reduction.distance
        
        log.warn(DEBUG_MSG_AVG + "Distance[" + sum + "]")
        sum
    }

    def bufferEncoder: Encoder[BufferPointsLocal] = Encoders.product
    def outputEncoder: Encoder[Double] = Encoders.scalaDouble

    //Calculate Distance and reduce buffer size
    def sumDistanceBetween(buffer: BufferPointsLocal, bufferSize: Int): BufferPointsLocal = {
        while(buffer.points.size > 1 && buffer.points.size > bufferSize) {
            val p1 = buffer.points(buffer.points.size)
            val p2 = buffer.points(buffer.points.size - 1)

            buffer.distance +=  distanceBetween(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z)
            buffer.points.drop(buffer.points.size)
        }
        buffer
    }

    //https://www.calculatorsoup.com/calculators/geometry-solids/distance-two-points.php
    def distanceBetween(p1: Position, p2: Position): Double = {
        distanceBetween(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z)
    }

    def distanceBetween(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double = {
        sqrt(pow(x2 - x1, 2) + pow(y2 - y1, 2) + pow(z2 - z1, 2))
    }
}
