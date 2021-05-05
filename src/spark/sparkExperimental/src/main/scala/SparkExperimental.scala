import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql._
import org.apache.spark.sql.types._

import org.apache.log4j.{Level, LogManager, PropertyConfigurator}
import org.apache.spark.sql.avro.functions._

import java.sql.Timestamp
import scala.io.Source

import org.apache.commons.logging.LogFactory

import transformations.TransTimestamp._
import transformations.TransWifi._
import transformations.TransOdom._

import aggregations.AggDistance
import sending.SendData._

object SparkExperimental {

    def main(args: Array[String]) {
        // Import config data
        import config.Config._
        
        // Config Logs
        log.setLevel(LOG_LEVEL)

        log.warn("###############################") 
        log.warn("###### SparkExperimental ######") 
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
                col("find3.findTimestamp").as(N_TIMESTAMP_FIND_UNIX),
                col("find3.odomData").as(N_ODEM_DATA),
                col("find3.wifiData").as(N_WIFI)
            )
        
        //Change format of the find timestamp
        val toTime = epochToTimeStamp(avroDataFrame, N_TIMESTAMP_FIND, N_TIMESTAMP_FIND_UNIX)
        //Create timestamp for HDS partition(Remove not allowed characters for HDFS)
        val hdfsTime = shortenTimestamp(toTime, N_TIMESTAMP_HDFS, N_TIMESTAMP_KAFKA_IN)

        //Here would be the save to the HDFS

        //Calculate the average wifi strenght
        val avgWifi = calculateWifiAverage(toTime, N_AVG_WIFI, N_WIFI)
        sendStream(avgWifi, BOOTSTRAP_SERVERS, TOPICS_WIFIDATA, CHECKPOINT_KAFKA_WIFIDATA)
        avgWifi.printSchema()
        
        //Aggegrate diffrent analytics about the wifi strenght
        val wifiData = avgWifi
            .groupBy(N_SENDERNAME, N_LOCATION)
            .agg(max(N_TIMESTAMP_KAFKA_IN), max(N_AVG_WIFI), min(N_AVG_WIFI), avg(N_AVG_WIFI), count(N_AVG_WIFI))
        sendStream(wifiData, BOOTSTRAP_SERVERS, TOPICS_WIFIANLY, CHECKPOINT_KAFKA_WIFIANLY)
        wifiData.printSchema()

        /*
        val senderWindow = avgWifi
            .groupBy(window(col(N_TIMESTAMP_KAFKA_IN), "10 minute", "1 minute"), col(N_SENDERNAME), col(N_LOCATION))
            .agg(max(N_AVG_WIFI), min(N_AVG_WIFI), avg(N_AVG_WIFI), count(N_AVG_WIFI))
            .sort("window")
        */

        //Explode the odometry data into a pretty table format
        val odom = explodeOdom(avgWifi, spark, JSON_SAMPLE, N_TIMESTAMP_KAFKA_IN, N_SENDERNAME, N_LOCATION, N_ODEM_DATA)
        sendStream(odom, BOOTSTRAP_SERVERS, TOPICS_ODOMCLEAN, CHECKPOINT_KAFKA_ODOMCLEAN)
        odom.printSchema()

        //Calculate the driving distance based of the odometry data
        val distance = calcDistance(odom, spark, "secs", "nanoSecs", N_SENDERNAME, "positionX", "positionY", "positionZ")
        sendStream(distance, BOOTSTRAP_SERVERS, TOPICS_ODOMDISTANCE, CHECKPOINT_KAFKA_ODOMDISTANCE)
        distance.printSchema()

        spark.streams.awaitAnyTermination()
    }
}