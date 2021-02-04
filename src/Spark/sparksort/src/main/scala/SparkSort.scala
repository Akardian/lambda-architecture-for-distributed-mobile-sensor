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
import java.sql.Date

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
            .select( //Flatten data structure
                col("timestamp").as("timestampKafkaIn"),
                col("find3.findTImestamp").as("timestampFind"),
                col("find3.senderName").as("senderName"),
                col("find3.location").as("location"),
                col("find3.gpsCoordinate").as("gpsCoordinate"),
                col("find3.wifiData.wifiData").as("wifiData")
            )
        avroDataFrame.printSchema()
        avroDataFrame.writeStream
            .outputMode("update")
            .format("console")
            .start()

        val avgWifiData = avroDataFrame//.select($"timestamp", $"find3.wifiData.wifiData")
            .withColumn(N_AVG_WIFI, aggregate(
                map_values(col("wifiData")), 
                lit(0), //set default value to 0
                (SUM, Y) => (SUM + Y)).cast(DoubleType) / size(col("wifiData")) //Calculate Average
            )
        avgWifiData.printSchema()

        val test = avgWifiData
            .groupBy()
            .agg(sum(N_AVG_WIFI))
        test.printSchema()

        test.writeStream
            .outputMode("complete")
            .format("console")
            .start()

        val count = avgWifiData
            .select($"timestampKafkaIn", $"avgWifi")
 

        count.writeStream
            .outputMode("update")
            .format("console")
            .start()
        /*
        Caused by: org.apache.spark.SparkException: Job aborted due to stage failure: Task 0 in stage 5.0 
        failed 4 times, most recent failure: Lost task 0.3 in stage 5.0 (TID 8, 172.21.0.8, executor 0): 
        org.apache.spark.SparkException: Dataset transformations and actions can only be invoked by the driver, 
        not inside of other Dataset transformations; for example, dataset1.map(x => dataset2.values.count() * x) is 
        invalid because the values transformation and count action cannot be performed inside of the dataset1.map transformation.
         For more information, see SPARK-28702.

        val wifiTotal = avgWifiData
            .map(row => {
                val curentTimestamp = row.getTimestamp(0)
                val firstTimestamp = avgWifiData
                    .groupBy("timestampKakfaIn")
                    .min("timestampKakfaIn")
                    .first()
                    .getTimestamp(0)

                val sumTotal = avgWifiData
                    .groupBy()
                    .sum("avgWifi")
                    .where(
                        unix_timestamp($"timestampKakfaIn").between(firstTimestamp, curentTimestamp))
                    .first()
                    .getDouble(0)
                (sumTotal)
            })*/

        /*
        val firstTimestamp = avgWifiData
            .groupBy("timestampKakfaIn")
            .agg(min($"timestampKakfaIn"))
            .first()
            .getTimestamp(0)
        */

        /*
        val wifiTotal = avgWifiData
                .withColumn("",
                    lit(
                    avgWifiData
                        .groupBy()
                        .sum("wifiAvg")
                        .where(
                            unix_timestamp($"timestampKakfaIn")
                                .between(firstTimestamp, $"timestampKakfaIn")
                            )
                        .first()
                        .getDouble(0)
                    )
                )
        */
        
        //val avgWindow = window($"timestampKafkaIn", "1 month")
            //.orderBy("timestampKakfaIn")

        /*
        val sumTotal =
            avgWifiData
            .groupBy()
            .sum(N_AVG_WIFI)
            .as(N_SUM_TOTAL)
        val wifiTotal = avgWifiData.select($"*", sumTotal)
        

        wifiTotal.printSchema()*/
        //$"kafkaInputTimestamp", $"senderName", $"location", $"findTimestamp", $"gpsCoordinate", $"wifiData", $"wifiAvg", 
        /*
        window($"timestamp", "10 seconds", "10 seconds"
        val totalAvg = avgWifiData
            .withColumn("totalAvg", avg("avgWifiData").over(totalAvgWindow)) 
        totalAvg.printSchema()
        */

        //val sortTimestamp = avgWifiData.sort($"timestamp")

        //val sortWifiData = sortTimestamp.sort($"avgWifiData")
/*
        wifiTotal.writeStream
            .outputMode("update")
            .format("console")
            .start()*/
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