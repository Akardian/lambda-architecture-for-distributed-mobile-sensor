package main

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
}
