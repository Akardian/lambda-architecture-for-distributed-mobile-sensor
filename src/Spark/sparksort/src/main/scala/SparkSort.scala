import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql._
import org.apache.spark.sql.types._

import org.apache.log4j.{Level, LogManager, PropertyConfigurator}
import org.apache.spark.sql.avro.functions._

import java.sql.Timestamp
import java.nio.file.Paths
import java.nio.file.Files
import scala.io.Source
import org.apache.commons.net.ntp.TimeStamp
import org.apache.spark.sql.expressions.Window

object SparkSort {

    def main(args: Array[String]) {
        // Import config data
        import config.Config._
        
        // Config Logs
        val log = LogManager.getRootLogger
        log.setLevel(LOG_LEVEL)
        log.info("###############################") 
        log.info("####### Worst Case Sort #######") 
        log.info("###############################")

        //BUild Spark Session
        val spark = SparkSession
            .builder()
            .appName(CONTEXT_NAME)
            .getOrCreate()       
        import spark.implicits._
        log.debug(DEBUG_MSG + "Building Spark Session")

        log.info("######### Sark Context Config #########")
        log.info(spark.sparkContext.getConf.toDebugString)

        //Read Avro Schema from Resource and convert it to a String
        val source = Source.fromResource(SCHEMA_PATH)
        log.debug(DEBUG_MSG + "Source is empty=" + source.isEmpty)
        val jsonFormatSchema = source.mkString
        log.debug(DEBUG_MSG + "Json Schema Format\n" + jsonFormatSchema)

        // Subscribe to Kafka topic
        log.debug(DEBUG_MSG + "Read stream from Kafka")
        val avroDataFrame = spark
            .readStream
            .format("kafka")
            .option("kafka.bootstrap.servers", BOOTSTRAP_SERVERS)
            .option("subscribe", TOPICS_INPUT)
            .load()
            .select(
                $"timestamp", //Keep Kafka Timestamp
                from_avro($"value", jsonFormatSchema).as("find3")) //Convert avro schema to Spark Data
        avroDataFrame.printSchema()

        val avgWifiData = avroDataFrame//.select($"timestamp", $"find3.wifiData.wifiData")
            .withColumn("avgWifiData", aggregate(
                map_values(col("find3.wifiData.wifiData")), 
                lit(0), 
                (SUM, Y) => (SUM + Y)).cast(DoubleType) / size(col("find3.wifiData.wifiData"))
            )
        avgWifiData.printSchema()

        val totalAvgWindow = Window
            .rangeBetween(Window.unboundedPreceding, Window.unboundedFollowing)
        val totalAvg = avgWifiData
            .withColumn("totalAvg", avg("avgWifiData").over(totalAvgWindow)) 
        totalAvg.printSchema()
            
        /*
        sed by: org.apache.spark.sql.AnalysisException: cannot resolve 
    '   aggregate(
            map_values(`find3`.`wifiData`.`wifiData`), 
            0, 
            lambdafunction((CAST((namedlambdavariable() + namedlambdavariable()) AS DOUBLE) / 
            CAST(size(`find3`.`wifiData`.`wifiData`) AS DOUBLE)), 
            
            namedlambdavariable(), namedlambdavariable()), lambdafunction(namedlambdavariable(), namedlambdavariable()))' due to data type mismatch: argument 3 requires int type, however, 'lambdafunction((CAST((namedlambdavariable() + namedlambdavariable()) AS DOUBLE) / CAST(size(`find3`.`wifiData`.`wifiData`) AS DOUBLE)), namedlambdavariable(), namedlambdavariable())' is of double type.;;
        */

        val query = totalAvg.writeStream
            .outputMode("update")
            .format("console")
            .start()
/*
        //Write Data to Kafka
        val query = wifiMap
            .selectExpr("CAST(timestamp AS STRING) as timestamp", "to_json(struct(*)) AS value")
            .writeStream
            .format("kafka")
            .outputMode("update")
            .option("kafka.bootstrap.servers", BOOTSTRAP_SERVERS)
            .option("topic", TOPICS_OUTPUT)
            .option("checkpointLocation", CHECKPOINT_KAFKA)
            .start()*/
        
        spark.streams.awaitAnyTermination()
    }
}