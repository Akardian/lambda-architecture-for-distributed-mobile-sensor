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

object SparkFind3Batch {

    def main(args: Array[String]) {
        // Import config data
        import config.Config._
        
        // Config Logs
        val log = LogManager.getRootLogger
        log.setLevel(LOG_LEVEL)
        log.warn("##############################") 
        log.warn("######### Find3Batch #########") 
        log.warn("##############################")

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
               
        // Create FileSystem object from Hadoop Configuration
        val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)

        log.warn(DEBUG_MSG + "Wokring dir: " + fs.getWorkingDirectory())
        log.warn(DEBUG_MSG + "Home dir: " + fs.getHomeDirectory)

        fs.setWorkingDirectory(new Path("file:///namenode:9000"))

        // Change file name from Spark generic to new one
        fs.rename(new Path(HDFS_PATH_NEW), new Path(HDFS_PATH_TMP))
       
    }
}