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
import java.util.HashMap
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.types.TimestampType

object SparkFind3 {

    def main(args: Array[String]) {
        // Import config data
        import config.Config._
        
        // Config Logs
        val log = LogManager.getRootLogger
        log.setLevel(LOG_LEVEL)
        log.warn("###############################") 
        log.warn("############ Find3 ############") 
        log.warn("###############################")

        //BUild Spark Session
        val spark = SparkSession
            .builder()
            .appName(CONTEXT_NAME)
            .getOrCreate()       
        import spark.implicits._
        log.warn(DEBUG_MSG + "Building Spark Session")

        //Read Avro Schema from Resource and convert it to a String
        val source = Source.fromResource(SCHEMA_PATH)
        log.warn(DEBUG_MSG + "Source is empty=" + source.isEmpty)
        val jsonFormatSchema = source.mkString
        log.warn(DEBUG_MSG + "Json Schema Format\n" + jsonFormatSchema)

        // Subscribe to Kafka topic
        log.warn(DEBUG_MSG + "Read stream from Kafka")
        val avroDataFrame = spark
            .readStream
            .format("kafka")
            .option("kafka.bootstrap.servers", BOOTSTRAP_SERVERS)
            .option("subscribe", TOPICS_INPUT)
            .load()
            .select(
                $"timestamp", //Keep Kafka Timestamp
                from_avro($"value", jsonFormatSchema).as("find3")) //Convert avro schema to Spark Data
            .select( //Flatten data structure
                col("timestamp").as(N_TIMESTAMP_KAFKA_IN),
                col("find3.senderName").as(N_SENDERNAME),
                col("find3.location").as(N_LOCATION),
                col("find3.findTimestamp").as(N_TIMESTAMP_FIND),
                col("find3.odomData").as(N_ODEM_DATA),
                col("find3.wifiData").as(N_WIFI)
            )
            log.warn(DEBUG_MSG + "find3Data")
        avroDataFrame.printSchema()
        
        //Create timestamp for HDS partition(Remove not allowed characters for HDFS)
        val hdfsDataFrame = avroDataFrame
            .withColumn("timestamp-hdfs", date_format(date_trunc("hour", $"timestampKafkaIn"), "yyyy-MM-dd HH-mm"))
            .withColumn(N_TIMESTAMP_KAFKA_IN, from_unixtime(col(N_TIMESTAMP_FIND),"MM-dd-yyyy HH:mm:ss"))

        //Calcutlate Average of wifiData
        val avgWifiData = avroDataFrame
            .withColumn(N_AVG_WIFI, aggregate(
                map_values(col(N_WIFI)), 
                lit(0), //set default value to 0
                (SUM, Y) => (SUM + Y)).cast(DoubleType) / size(col(N_WIFI)) //Calculate Average
            )
        avgWifiData.printSchema()

        //Write RAW data to HDFS
        hdfsDataFrame.writeStream  
            .format("json")
            .outputMode("append")
            .partitionBy("timestamp-HDFS")
            .option("path", HDFS_PATH)
            .option("checkpointLocation", CHECKPOINT_HDFS)
            .start()
        hdfsDataFrame.printSchema()

        //Write Data to Kafka
        val query = hdfsDataFrame
            .selectExpr("CAST(timestampKafkaIn AS STRING) as timestamp", "to_json(struct(*)) AS value")
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