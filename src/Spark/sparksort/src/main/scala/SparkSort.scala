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
            .config("spark.executor.id", NAME + "-driver")
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
        log.debug(DEBUG_MSG + "find3Data")
        avroDataFrame.printSchema()
        
        //Create timestamp for HDS partition(Remove not allowed characters for HDFS)
        //val hdfsDataFrame = avroDataFrame
        //    .withColumn("time", date_format(date_trunc("hour", $"timestamp"), "yyyy-MM-dd HH-mm"))

        val wifiMap = avroDataFrame.select($"find3.wifiData.wifiData")
            .as[Map[String, Int]]
            .map(row => {
                (row, 1) //log.debug("" + row.toString())
            })
        wifiMap.printSchema()
                //.as[Map<String,Integer>]  )
/*
        val avarage = avroDataFrame
            .withColumn(
                "avarage",
                
            )

        val sortTimestamp = avroDataFrame
            .sort("timestamp")
        sortTimestamp.printSchema();*/

        
        val out = wifiMap

        val query = out.writeStream //Print to console for Debug
            .outputMode("update")
            .format("console")
            .start()    

        //Write Data to Kafka
        /*val query = hdfsDataFrame
            .selectExpr("CAST(timestamp AS STRING) as timestamp", "to_json(struct(*)) AS value")
            .writeStream
            .format("kafka")
            .outputMode("update")
            .option("kafka.bootstrap.servers", BOOTSTRAP_SERVERS)
            .option("topic", TOPICS_OUTPUT)
            .option("checkpointLocation", CHECKPOINT_KAFKA)
            .start() */
        
        spark.streams.awaitAnyTermination()
    }
}