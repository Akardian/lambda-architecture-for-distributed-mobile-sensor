import org.apache.spark.sql.SparkSession

object Test {
    val GROUP_ID = "Test-Spark"
    val TOPICS_OUTPUT = "kafka-test-tranformed-topic"
    val TOPICS_INPUT = "kafka-test-topic"
    val CONTEXT_NAME = "WordCountTest"
    val BOOTSTRAP_SERVERS = "kafka-01:9092";

    def main(args: Array[String]): Unit = {
        val spark = SparkSession
            .builder()
            .appName(CONTEXT_NAME)
            .getOrCreate()       
        // For implicit conversions
        import spark.implicits._

        // Subscribe to Kafka topic
        val dataFrame = spark
            .readStream
            .format("kafka")
            .option("kafka.bootstrap.servers", BOOTSTRAP_SERVERS)
            .option("subscribe", TOPICS_INPUT)
            .load()
        dataFrame.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
            .as[(String, String)]

        dataFrame.show();

        // Write key-value data from a DataFrame to a specific Kafka topic specified in an option
        val dataSet = dataFrame
            .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
            .writeStream
            .format("kafka")
            .option("kafka.bootstrap.servers", BOOTSTRAP_SERVERS)
            .option("topic", TOPICS_OUTPUT)
            .start()
    }
}
