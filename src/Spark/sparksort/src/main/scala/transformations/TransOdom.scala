package transformations

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql._
import org.apache.spark.sql.types._

import config.Config._

object TransOdom {
  
    /**
      * Explodes a array of odom data and splitts all values into its own column. Drops the odom array.
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
}
