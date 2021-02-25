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
import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec.Q

object SparkSort {

    def main(args: Array[String]) {
        // Import config data
        import config.Config._
        
        // Config Logs
        log.setLevel(LOG_LEVEL)
        log.warn("###############################") 
        log.warn("####### Worst Case Sort #######") 
        log.warn("###############################")

        //BUild Spark Session
        val spark = SparkSession
            .builder()
            .appName(CONTEXT_NAME)
            .getOrCreate()       
        import spark.implicits._
        log.warn(DEBUG_MSG + "Building Spark Session")

        log.warn("######### Sark Context Config #########")
        log.warn(spark.sparkContext.getConf.toDebugString)

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
                col("find3.findTImestamp").as(N_TIMESTAMP_FIND),
                col("find3.senderName").as(N_SENDERNAME),
                col("find3.location").as(N_LOCATION),
                col("find3.gpsCoordinate").as(N_GPS),
                col("find3.wifiData.wifiData").as(N_WIFI)
            )
        avroDataFrame.printSchema()
        avroDataFrame.writeStream
            .outputMode("update")
            .option("truncate", "true")
            .format("console")
            .start()

        val avgWifiData = avroDataFrame//.select($"timestamp", $"find3.wifiData.wifiData")
            .withColumn(N_AVG_WIFI, aggregate(
                map_values(col(N_WIFI)), 
                lit(0), //set default value to 0
                (SUM, Y) => (SUM + Y)).cast(DoubleType) / size(col(N_WIFI)) //Calculate Average
            )
        avgWifiData.printSchema()

        val rollingAvg = avgWifiData.select(col("timestampKafkaIn").as("timestamp"), col("wifiAvg").as("wifiAvg")).as[WifiData]
        rollingAvg.printSchema()

        // Convert the function to a `TypedColumn` and give it a name
        val averageSalary = MyRollingAvg.toColumn.name("rollingAvg")
        val result = rollingAvg.select(averageSalary)
        result.printSchema()
            
        val exMap = result.select(explode($"entryMap"))
        exMap.printSchema()

        exMap.writeStream
            .outputMode("update")
            .option("truncate", "false")
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
            .min("timestampKakfaIn")

        firstTimestamp.writeStream
            .outputMode("complete")
            .format("console")
            .start()

        avgWifiData.withColumn("avg", aggregate(
            col(N_AVG_WIFI), 
            lit(0),
            (acc, x) => ()
        ))*/

        /*
        avgWifiData
            .groupBy("timestampKafkaIn", "avgWifi")
            .sum("avgWifi")
            .where(
                unix_timestamp($"timestampKakfaIn").between(firstTimestamp, $"timestampKakfaIn")
                )
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