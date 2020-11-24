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
import scala.concurrent.duration._

object SparkAvroConsumer {
    val DEBUG_MSG = "Avro Consumer: "

    val GROUP_ID = "Avro-Spark"
    val TOPICS_INPUT = "test-data-generator-input"
    val TOPICS_OUTPUT = "test-data-generator-output"
    val CONTEXT_NAME = "Scala Avro Consumnmer"
    val BOOTSTRAP_SERVERS = "kafka01:9092";
    
    val WATERMARK_SIZE = 5.seconds
    val WINDOW_SIZE = 10.seconds
    val SLIDE_SZIZE = 10.seconds

    def main(args: Array[String]) {
        val log = LogManager.getRootLogger
        log.setLevel(Level.INFO)

        log.info("###############################") 
        log.info("####### Scale Avro Test #######") 
        log.info("###############################")

        log.debug(DEBUG_MSG + "Building Spark Session")
        val spark = SparkSession
            .builder()
            .appName(CONTEXT_NAME)
            .getOrCreate()       
        // For implicit conversions
        import spark.implicits._

        //Read Avro Schema from Resource and convert it to a String
        val source = Source.fromResource("avro/TestData.avsc")
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
                from_avro($"value", jsonFormatSchema).as("testData")) //Convert avro schema to Spark Data

        avroDataFrame.printSchema()
        log.debug(DEBUG_MSG + "Avro Data")
        
        //Create timestamp for HDS partition(Remove not allowed characters for HDFS)
        val hdfsDataFrame = avroDataFrame
            .withColumn("time", date_format(date_trunc("hour", $"timestamp"), "yyyy-MM-dd HH-mm"))

        //Write RAW data to HDFS
        hdfsDataFrame.writeStream  
            .format("json")
            .outputMode("append")
            .partitionBy("time")
            .option("path", "hdfs://namenode:9000/user/haw/testData/new-data")
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
            .withWatermark("timestamp", WATERMARK_SIZE.toString()) //set watermark for checkpoint and windowsize for grouping
            .groupBy(
                $"timestamp", $"word") //GroupBy word and timestamp
            .count()
        groupData.printSchema()
        
        val query = groupData
            .selectExpr("CAST(timestamp AS STRING) as timestamp", "to_json(struct(*)) AS value")
            .writeStream
            .format("kafka")
            .trigger(Trigger.ProcessingTime("2 seconds"))
            .outputMode("append")
            .option("kafka.bootstrap.servers", BOOTSTRAP_SERVERS)
            .option("topic", TOPICS_OUTPUT)
            .option("checkpointLocation", "hdfs://namenode:9000/user/haw/testData/checkpoint/kafka")
            .start() 
        
        spark.streams.awaitAnyTermination()
    }
}