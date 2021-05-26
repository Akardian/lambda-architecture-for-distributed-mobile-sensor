package transformations

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql._
import org.apache.spark.sql.types._

 import config.Config._

object TransTimestamp {

    /**
    * Adds column with shortend timestamp "MM-dd-yyyy HH:mm" to DataFrame
    *
    * @param dataFrame DataFrame with Timestamp Column
    * @param name Name of the new Column
    * @param timeStampColumn Column name of the source timestamp
    * @return DataFrame with new Timestamp Column
    */
    def shortenTimestamp(dataFrame: DataFrame, name: String, timeStampColumn: String) : DataFrame = {
        dataFrame.withColumn(name, to_timestamp(date_trunc("hour", col(timeStampColumn)), "MM-dd-yyyy_HH-mm"))
    }

    /**
      * Converts a epoch time Column into a Timestamp Column and drops the old one
      *
      * @param dataFrame DataFrame with Timestamp Column
      * @param name Name of the new Column
      * @param timeStampColumn Column name of the source timestamp
      * @return DataFrame with new Timestamp Column
      */
    def epochToTimeStamp(dataFrame: DataFrame, name: String, timeStampColumn: String) : DataFrame = {
        dataFrame
            .withColumn(name, from_unixtime(col(timeStampColumn), "MM-dd-yyyy HH:mm:ss"))
            .drop(timeStampColumn)
        
    }
}
