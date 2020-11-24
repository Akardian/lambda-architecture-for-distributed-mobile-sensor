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

object SparkAvroConsumer {
    val DEBUG_MSG = "Avro Batch Task: "
    
    val CONTEXT_NAME = "Scala Avro Batch Task"

    val LOG_LEVEL = Level.Debug;

    val WINDOW_SIZE = "30 seconds"
    val SLIDE_SZIZE = "30 seconds"

    def main(args: Array[String]) {
        val log = LogManager.getRootLogger
        log.setLevel(LOG_LEVEL)

        log.info("###############################") 
        log.info("#### Spark Avro Batch Task ####") 
        log.info("###############################")

        log.debug(DEBUG_MSG + "Building Spark Session")
        val spark = SparkSession
            .builder()
            .appName(CONTEXT_NAME)
            .getOrCreate()       
        // For implicit conversions
        import spark.implicits._
       
        //Write RAW data to HDFS
        avroDataFrame.writeStream
            .format("avro")
            .option("path", "hdfs://namenode:9000/user/haw/testData/data")
            .option("checkpointLocation", "hdfs://namenode:9000/user/haw/testData/checkpoint/hdfs")
            .start()

        val messageData = avroDataFrame
            .select($"timestamp", $"testData.message")
            .as[(Timestamp, String)] //Select message as String and timestamp
            .flatMap(line => line._2.split(" ") //Split lines 
            .map(word => (line._1, word))) //Kombine the splitt words with the timestamp
            .toDF("timestamp", "word")
        messageData.printSchema()

        val groupData = messageData
            .withWatermark("timestamp", WINDOW_SIZE) //set watermark for checkpoint and windowsize for grouping
            .groupBy("timestamp", "word") //GroupBy word and timestamp
            .count()
        groupData.printSchema()
    
        val query = groupData.writeStream //Print to consle for Debug
            .outputMode("complete")
            .format("console")
            .start()
    }
}