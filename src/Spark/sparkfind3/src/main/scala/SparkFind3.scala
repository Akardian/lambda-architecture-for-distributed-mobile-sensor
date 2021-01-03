import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import org.apache.log4j.{Level, LogManager, PropertyConfigurator}
import org.apache.spark.sql.avro.functions._

import java.sql.Timestamp
import java.nio.file.Paths
import java.nio.file.Files
import scala.io.Source
import org.apache.commons.net.ntp.TimeStamp
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.streaming.OutputMode

object SparkFind3 {

    def main(args: Array[String]) {
        // Import config data
        import config.Config._
        
        // Config Logs
        val log = LogManager.getRootLogger
        log.setLevel(LOG_LEVEL)
        log.info("###############################") 
        log.info("############ Find3 ############") 
        log.info("###############################")

        //BUild Spark Session
        val spark = SparkSession
            .builder()
            .appName(CONTEXT_NAME)
            .getOrCreate()       
        import spark.implicits._
        log.debug(DEBUG_MSG + "Building Spark Session")

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
        val hdfsDataFrame = avroDataFrame
            .withColumn("time", date_format(date_trunc("hour", $"timestamp"), "yyyy-MM-dd HH-mm"))

        //Write RAW data to HDFS
        hdfsDataFrame.writeStream  
            .format("json")
            .outputMode("append")
            .partitionBy("time")
            .option("path", HDFS_PATH)
            .option("checkpointLocation", CHECKPOINT_HDFS)
            .start()
        
        //Write Data to Kafka
        val query = hdfsDataFrame
            .selectExpr("CAST(timestamp AS STRING) as timestamp", "to_json(struct(*)) AS value")
            .writeStream
            .format("kafka")
            .outputMode("update")
            .option("kafka.bootstrap.servers", BOOTSTRAP_SERVERS)
            .option("topic", TOPICS_OUTPUT)
            .option("checkpointLocation", CHECKPOINT_KAFKA)
            .start() 
        
        spark.streams.awaitAnyTermination()
    }
}