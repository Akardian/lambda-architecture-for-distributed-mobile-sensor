import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import org.apache.log4j.{Level, LogManager, PropertyConfigurator}

import java.sql.Timestamp

object MyKafkaTest {
    val GROUP_ID = "Test-Spark"
    val TOPICS_OUTPUT = "kafka-test-tranformed-topic"
    val TOPICS_INPUT = "kafka-test-topic"
    val CONTEXT_NAME = "Scala Streaming Test"
    val BOOTSTRAP_SERVERS = "kafka-01:9092";
    val WINDOW_SIZE = "30 seconds"
    val SLIDE_SZIZE = "30 seconds"

    def main(args: Array[String]) {
        val log = LogManager.getRootLogger
        log.setLevel(Level.DEBUG)

        log.info("##############################") 
        log.info("#### Scale Streaming Test ####") 
        log.info("##############################")

        log.debug("Building Spark Session")
        val spark = SparkSession
            .builder()
            .appName(CONTEXT_NAME)
            .getOrCreate()       
        // For implicit conversions
        import spark.implicits._

        // Subscribe to Kafka topic
        log.debug("Read stream from Kafka")
        val dataFrame = spark
            .readStream
            .format("kafka")
            .option("kafka.bootstrap.servers", BOOTSTRAP_SERVERS)
            .option("subscribe", TOPICS_INPUT)
            .load()
        dataFrame.selectExpr("CAST(value AS STRING)", "CAST(timestamp AS Timestamp)")
            .as[(String, Timestamp)]
        dataFrame.printSchema()

        //Splitt words and keep timestamp
        val words = dataFrame
            .selectExpr("CAST(value AS STRING)", "CAST(timestamp AS Timestamp)")
            .as[(String, Timestamp)]
            .flatMap(line => line._1.split(" ")
            .map(word => (word, line._2)))
            .toDF("value", "timestamp")
        
        words.printSchema()

        //Count Words https://sparkbyexamples.com/spark/using-groupby-on-dataframe/
        val wordcounts = words
            .withWatermark("timestamp", WINDOW_SIZE)
            .groupBy("value", "timestamp")
            .count()

        log.debug("Show stream")
        wordcounts.printSchema()

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

        query.awaitTermination();
    }
}
