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
import java.sql.Time
import org.apache.commons.logging.LogFactory
import org.apache.spark.SparkContext

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

        //Set Executer log level
        spark.sparkContext.parallelize(Seq("")).foreachPartition(x => {
            LogManager.getRootLogger().setLevel(LOG_LEVEL)

            val log = LogFactory.getLog("EXECUTOR-LOG:")
            log.warn(DEBUG_MSG + "Executer log level set to" + LOG_LEVEL)
        })

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
                col("find3.senderName").as(N_SENDERNAME),
                col("find3.location").as(N_LOCATION),
                col("find3.findTimestamp").as(N_TIMESTAMP_FIND),
                col("find3.odomData").as(N_ODEM_DATA),
                col("find3.wifiData").as(N_WIFI)
            )
        
        //Create timestamp for HDS partition(Remove not allowed characters for HDFS) and change format of the find timestamp
        val hdfsDataFrame = avroDataFrame
            .withColumn(N_TIMESTAMP_HDFS, to_timestamp(date_trunc("hour", col(N_TIMESTAMP_KAFKA_IN)), "MM-dd-yyyy HH:mm"))
            .withColumn(N_TIMESTAMP_FIND, to_timestamp(from_unixtime(col(N_TIMESTAMP_FIND)), "MM-dd-yyyy HH:mm:ss.SSSS"))

        val prettyPrint = hdfsDataFrame.drop(N_WIFI) //Drop for pretty print

        //Here would be the save to the HDFS
        prettyPrint.writeStream
            .outputMode("update")
            .option("truncate", "false")
            .format("console")
            .start()

        val avgWifiData = hdfsDataFrame
            .withColumn(N_AVG_WIFI, aggregate(
                map_values(col(N_WIFI)), 
                lit(0), //set default value to 0
                (SUM, Y) => (SUM + Y)).cast(DoubleType) / size(col(N_WIFI)) //Calculate Average
            )
        avgWifiData.printSchema()

        //Select columns rolling Average calculation and rename
        val rollingAvg = avgWifiData
            .select(col(N_TIMESTAMP_KAFKA_IN).as("timestamp"), col(N_AVG_WIFI).as("wifiAvg"))
            .withColumn("rollingAvg", lit(0))
            .as[WifiData]
        rollingAvg.printSchema()

        // Convert the function to a `TypedColumn` and give it a name
        val averageSalary = MyRollingAvg.toColumn.name("rollingAvg")
        val v = rollingAvg.select(averageSalary)
        val exMap = rollingAvg
            .select(averageSalary)
            .select(explode('list))
            .select($"col._1".as("timestamp"), $"col._2".as("sum"))

        exMap.writeStream
            .outputMode("update")
            .option("truncate", "false")
            .format("console")
            .start() 
        
        val b = rollingAvg
            .withColumn("sum", sum(rollingAvg("wifiAvg"))
                .over(Window.rowsBetween(Window.unboundedPreceding, Window.currentRow))
            )
            

        b.writeStream
            .outputMode("update")
            .option("truncate", "false")
            .format("console")
            .start() 
        
        spark.streams.awaitAnyTermination()
    }
}