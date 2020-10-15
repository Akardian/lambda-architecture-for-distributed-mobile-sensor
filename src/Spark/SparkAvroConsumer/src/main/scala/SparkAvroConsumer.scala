import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import org.apache.log4j.{Level, LogManager, PropertyConfigurator}
import org.apache.spark.sql.avro.functions._

import java.sql.Timestamp
import java.nio.file.Paths
import java.nio.file.Files
import scala.io.Source
import org.apache.commons.net.ntp.TimeStamp

object SparkAvroConsumer {
    val DEBUG_MSG = "Avro Consumer: "

    val GROUP_ID = "Avro-Spark"
    val TOPICS_INPUT = "test-data-generator"
    val TOPICS_OUTPUT = "test-data-generator-output"
    val CONTEXT_NAME = "Scala Avro Consumnmer"
    val BOOTSTRAP_SERVERS = "kafka-01:9092";
    
    val WINDOW_SIZE = "30 seconds"
    val SLIDE_SZIZE = "30 seconds"

    def main(args: Array[String]) {
        val log = LogManager.getRootLogger
        log.setLevel(Level.DEBUG)

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
                $"timestamp",
                from_avro($"value", jsonFormatSchema).as("testData")) //Convert avro schema to Spark Data

        avroDataFrame.printSchema()
        log.debug(DEBUG_MSG + "Avro Data")
        
        // Daten auf hadoop schreiben

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

            query.awaitTermination();
    }
}

/*
        //Create JSON dataset https://spark.apache.org/docs/latest/sql-data-sources-json.html
        //val jsonData = spark.read.json(wordcounts)
        //jsonData.printSchema()

        val query = wordcounts
            .selectExpr("CAST(timestamp AS STRING) as timestamp", "to_json(struct(*)) AS value")
            .writeStream
            .format("kafka")
            .option("kafka.bootstrap.servers", BOOTSTRAP_SERVERS)
            .option("topic", TOPICS_OUTPUT)
            .option("checkpointLocation", "hdfs://namenode:9000/user/haw/checkpointLocation/MyKafkaTest")
            .start()
*/