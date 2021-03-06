package transformations

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql._
import org.apache.spark.sql.types._

import config.Config._
import aggregations.AggRollingAvg

object TransWifi {
  
    /**
      * Calculates the average wifi signal to all access points and writes it in new row
      *
      * @param dataframe DataFrame with a wifiData column
      * @param name Name of the new row
      * @param wifiColumn Name of the Wifi Data Column
      * @return Dataframe with new row
      */
    def calculateWifiAverage(dataframe: DataFrame, name: String, wifiColumn: String) : DataFrame = {
        val avgWifi = dataframe.withColumn(name, aggregate(
            map_values(col(wifiColumn)), 
            lit(0), //set default value to 0
            (SUM, Y) => (SUM + Y)).cast(DoubleType) / size(col(wifiColumn)) //Calculate Average
        )
        avgWifi.drop(col(wifiColumn))
    }

    /**
      * This is a test method to calculate the total running Avergage (Use with caution)
      *
      * @param spark SparkSession
      * @param dataframe dataframe with timestamp and wifiData
      * @param timestampColumn Timestamp column name
      * @param averageWifiColumn average wifi column name
      * @return Dataframe of timestamp and running average
      */
    def runningAverage(spark: SparkSession, dataframe: DataFrame, timestampColumn: String, averageWifiColumn: String) : DataFrame = {
        import spark.implicits._

        val rollingAvg = dataframe
            .select(col(N_TIMESTAMP_KAFKA_IN).as("timestamp"), col(N_AVG_WIFI).as("wifiAvg"))
            .as[WifiData]

        // Convert the function to a `TypedColumn` and give it a name
        val averageSalary = AggRollingAvg.toColumn.name("rollingAvg")

        rollingAvg
            .select(averageSalary)
            .select(explode('list))
            .select($"col._1".as("timestamp"), $"col._2".as("sum"))
    }
}
