import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql._
import org.apache.spark.sql.types._

import org.apache.log4j.{Level, LogManager, PropertyConfigurator}
import org.apache.spark.sql.avro.functions._

import org.apache.commons.logging.LogFactory

import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path

import transformations.TransTimestamp._
import transformations.TransWifi._
import transformations.TransOdom._

import aggregations.AggDistance
import sending.SendData._

import config.{PathConfig, Config}
import java.io.FileNotFoundException

object SparkFind3Batch {

    def main(args: Array[String]) {
        val pathConfig = PathConfig(args(0), args(1))
        import pathConfig._
        import Config._
        
        // Config Logs
        val log = LogManager.getRootLogger
        log.setLevel(LOG_LEVEL)
        log.warn("##############################") 
        log.warn("######### Find3Batch #########") 
        log.warn("##############################")
        log.warn("NAME[" + NAME + "[ HDFS_DATA[" + HDFS_DATA +"]")

        //BUild Spark Session
        val spark = SparkSession
            .builder()
            .appName(CONTEXT_NAME)
            .getOrCreate()       
        import spark.implicits._
        log.warn(DEBUG_MSG + "Building Spark Session")

        //Set Executer log level
        spark.sparkContext.parallelize(Seq("")).foreachPartition(x => {
            LogManager.getRootLogger().setLevel(LOG_LEVEL)

            val log = LogFactory.getLog("EXECUTOR-LOG:")
            log.warn(DEBUG_MSG + "Executer log level set to" + LOG_LEVEL)
        })

        log.warn("######### Sark Context Config #########")
        log.warn(spark.sparkContext.getConf.toDebugString)

        //Load data tmp data to compact
        try {
            // Read will throw Error if directory is empty
            val newData = spark.read
                .format("avro")
                .load(HDFS_PATH_LOAD)
            newData.printSchema()
                
            //Save data back in a compacted format
            newData.write
                .format("avro")
                .mode("append")
                .save(HDFS_PATH_SAVE)
        }catch {
            case ae: FileNotFoundException =>
            log.warn(DEBUG_MSG + "Failed to read directory. No new data to read?")
        }

        //Load all data
        val data = spark.read
            .format("avro")
            .load(HDFS_PATH_SAVE)
        data.printSchema()
        data.describe().show()
        data.show()

        val time = data
            .agg(max(N_TIMESTAMP_KAFKA_IN))
            //.select(date_trunc("hour", col("max(" + N_TIMESTAMP_KAFKA_IN + ")")).as("trunc"))
        time.printSchema()
        time.show()
        time.write
                .format("json")
               .mode("overwrite")
               .save(HDFS_PATH_TIME)

        val avgWifi = calculateWifiAverage(data, N_AVG_WIFI, N_WIFI)
        avgWifi.printSchema()
        avgWifi.describe().show()
        avgWifi.show()
        avgWifi.write
               .format("json")
               .mode("overwrite")
               .save(HDFS_PATH_AVG)

        //Aggegrate diffrent analytics about the wifi strenght
        val wifiData = avgWifi
            .groupBy(N_SENDERNAME, N_LOCATION)
            .agg(max(N_TIMESTAMP_KAFKA_IN), max(N_AVG_WIFI), min(N_AVG_WIFI), avg(N_AVG_WIFI), count(N_AVG_WIFI))
        wifiData.printSchema()
        wifiData.describe().show()
        wifiData.show()
        wifiData.write
               .format("json")
               .mode("overwrite")
               .save(HDFS_PATH_DATA)

        //Explode the odometry data into a pretty table format
        val odom = explodeOdom(avgWifi, spark, JSON_SAMPLE, N_TIMESTAMP_KAFKA_IN, N_SENDERNAME, N_LOCATION, N_ODEM_DATA)
        odom.printSchema()
        odom.describe().show()
        odom.show()
        odom.write
               .format("json")
               .mode("overwrite")
               .save(HDFS_PATH_ODOM)

        //Calculate the driving distance based of the odometry data
        val distance = calcDistance(odom, spark, N_TIMESTAMP_KAFKA_IN, "secs", "nanoSecs", N_SENDERNAME, "positionX", "positionY", "positionZ")
        val cleanDistance = distance.select(
                col("sendername").as("sendername"),
                col("max(timestampKafkaIn)").as("timestamp"),
                col("aggdistance$(secs, nsecs, x, y, z)").as("distance"))
        cleanDistance.printSchema()
        cleanDistance.describe().show()
        cleanDistance.show()
        cleanDistance.write
               .format("json")
               .mode("overwrite")
               .save(HDFS_PATH_DIST)
        
    }
}