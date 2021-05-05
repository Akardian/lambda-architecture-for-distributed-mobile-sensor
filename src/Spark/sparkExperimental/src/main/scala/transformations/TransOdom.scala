package transformations

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql._
import org.apache.spark.sql.types._

import config.Config._
import aggregations.AggDistance

object TransOdom {
  
    /**
      * Explodes an array of odom data and splitts all values into its own column. Drops the odom array.
      *
      * @param dataframe Dataframe to use the function on
      * @param spark Current Spark Session
      * @param jsonSample Sample String of one odom data set
      * @param timestamp Name of the timestamp column
      * @param senderName Name of the senderName column
      * @param location Name of the location column
      * @param odomColumn Name of the odom column
      * @return Dataframe with timestamp, senderName, location, split odom data
      */
    def explodeOdom(dataframe: DataFrame, spark: SparkSession, jsonSample: String, timestamp: String, senderName: String, location: String, odomColumn: String) : DataFrame = {
        import spark.implicits._

        val odomJson = dataframe
            .select(col(timestamp), col(senderName), col(location), explode(col(odomColumn)).as("odomJson"))

        val jsondf = spark.read.json(Seq(jsonSample).toDS) //jsondf.schema has the nested json structure we need
      
        val odom = odomJson.withColumn("odom", from_json(col("odomJson"), jsondf.schema))
        val odomDrop = odom
            .select(
                col(N_TIMESTAMP_KAFKA_IN), 
                col(N_SENDERNAME), 
                col(N_LOCATION), 
                col("odom.header.seq").as("seq"), 
                col("odom.header.frame_id").as("frame_id"),
                col("odom.header.stamp.secs").as("secs"), 
                col("odom.header.stamp.nsecs").as("nanoSecs"), 
                col("odom.pose.position.x").as("positionX"), 
                col("odom.pose.position.y").as("positionY"),
                col("odom.pose.position.z").as("positionZ"),
                col("odom.pose.orientation.x").as("orientationX"),
                col("odom.pose.orientation.y").as("orientationY"),
                col("odom.pose.orientation.z").as("orientationZ"),
                col("odom.pose.orientation.w").as("orientationW")
            )

        odomDrop
    }

    /**
      * Calculates the distance between points. Needs to be a OdomPoint compatible Dataframe
      * 
      * case class OdomPoint(val senderName: String, val secs: Long, val nsecs: Long, val x: Double, val y: Double, val z: Double)
      * 
      * @param dataframe
      * @param spark Sparksession
      * @param secs Name for Column secs
      * @param nSecs Name for Column nSecs
      * @param x Name for Column x
      * @param y Name for Column y
      * @param z Name for Column z
      * @return
      */
    def calcDistance(dataframe: DataFrame, spark: SparkSession, secs: String, nSecs: String, senderName: String, x: String, y: String, z: String) : DataFrame = {
        import spark.implicits._
        
        //Create typed DataSet
        val typedOdom = dataframe.select(
            col(senderName), 
            col(secs), 
            col(nSecs).as("nsecs"), 
            col(x).as("x"), 
            col(y).as("y"), 
            col(z).as("z")
        ).as[OdomPoint]

        val udafDistance = udaf(AggDistance)
        val distance = typedOdom
            .groupBy(col(senderName))
            .agg(max($"secs"), udafDistance($"secs", $"nsecs", $"x", $"y", $"z"))

        distance
    }
}
