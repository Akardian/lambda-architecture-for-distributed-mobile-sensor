import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Level, LogManager, PropertyConfigurator}

object MyKafkaTest {
    val GROUP_ID = "Test-Spark"
    val TOPICS_OUTPUT = "kafka-test-tranformed-topic"
    val TOPICS_INPUT = "kafka-test-topic"
    val CONTEXT_NAME = "Scala Streaming Test"
    val BOOTSTRAP_SERVERS = "kafka-01:9092";

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
        dataFrame.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
            .as[(String, String)]

        log.debug("Show stream")
        val query = dataFrame
            .writeStream
            .outputMode("complete")
            .format("console")
            .start()

        // Write key-value data from a DataFrame to a specific Kafka topic specified in an option
        /*log.debug("Write stream to Kafka")
        val dataSet = dataFrame
            .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
            .writeStream
            .format("kafka")
            .option("kafka.bootstrap.servers", BOOTSTRAP_SERVERS)
            .option("topic", TOPICS_OUTPUT)
            .start()*/

        dataSet.awaitTermination();
    }
}
