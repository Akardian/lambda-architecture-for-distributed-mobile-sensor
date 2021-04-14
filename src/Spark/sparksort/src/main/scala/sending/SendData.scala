package sending

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql._
import org.apache.spark.sql.types._


object SendData {

    /**
      * Prints stream in spark console with output mode update
      *
      * @param dataframe what to print
      * @param truncate "true" or "false"
      */
    def printStream(dataframe: DataFrame, truncate: String) {
        dataframe.writeStream
            .outputMode("update")
            .option("truncate", truncate)
            .format("console")
            .start() 
    }

    /**
      * Prints stream in spark console with output mode update and truncat "true"
      *
      * @param dataframe what to print
      */
    def printStream(dataframe: DataFrame) {
        printStream(dataframe, "true")
    }

	/**
	  * Send streaming data to kafka
	  *
	  * @param dataframe what to pirnt
	  * @param outputMode Output mode "update" or "append"
	  * @param server Server address
	  * @param topic Kafka topic
	  * @param checkpoint hdfs path for checkpoint location
	  */
	def sendStream(dataframe: DataFrame, outputMode: String, server: String, topic: String, checkpoint: String) {
        dataframe
            .selectExpr("CAST(timestampKafkaIn AS STRING) as timestamp", "to_json(struct(*)) AS value")
            .writeStream
            .format("kafka")
            .outputMode(outputMode)
            .option("kafka.bootstrap.servers", server)
            .option("topic", topic)
            .option("checkpointLocation", checkpoint)
            .start() 
	} 

	/**
	  * Send streaminf data to kafka with output mode "update"
	  *
	  * @param dataframe What to pirnt
	  * @param server Server address
	  * @param topic Kafka topic
	  * @param checkpoint hdfs path for checkpoint location
	  */
	def sendStream(dataframe: DataFrame, server: String, topic: String, checkpoint: String) {
		sendStream(dataframe, "update", server, topic, checkpoint)
	}
}
