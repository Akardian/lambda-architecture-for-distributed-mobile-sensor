import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql._
import org.apache.spark.sql.types._

import org.apache.log4j.{Level, LogManager, PropertyConfigurator}
import org.apache.spark.sql.avro.functions._

import org.apache.commons.logging.LogFactory

import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path

import transformations.TransTimestamp._
import transformations.TransWifi._
import transformations.TransOdom._

import aggregations.AggDistance
import sending.SendData._

import config.{PathConfig, Config}

object SparkFind3Batch {

    def main(args: Array[String]) {
        val pathConfig = PathConfig(args(0), args(1))
        import pathConfig._
        import Config._
        
        // Config Logs
        val log = LogManager.getRootLogger
        log.setLevel(LOG_LEVEL)
        log.warn("##############################") 
        log.warn("######### Find3Batch #########") 
        log.warn("##############################")
        log.warn("NAME[" + NAME + "[ HDFS_DATA[" + HDFS_DATA +"]")

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

        //Load data tmp data to compact
        val newData = spark.read
            .format("json")
            .option("multiline", "true")
            .load(HDFS_PATH_LOAD)
        if(!newData.isEmpty) {
            newData.printSchema()
            
            //Save data back in a compacted format
            newData.write
                .format("json")
                .mode("append")
                .save(HDFS_PATH_SAVE)
        }

        //Load all data
        val data = spark.read
            .format("json")
            .option("multiline", "true")
            .load(HDFS_PATH_SAVE)
        data.printSchema()
        data.show()

        //val avgWifi = calculateWifiAverage(data, N_AVG_WIFI, N_WIFI)
        //avgWifi.printSchema()
        
    }
}