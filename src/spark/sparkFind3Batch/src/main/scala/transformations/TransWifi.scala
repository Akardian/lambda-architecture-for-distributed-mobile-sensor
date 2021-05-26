package transformations

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql._
import org.apache.spark.sql.types._

import config.Config._

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
}
