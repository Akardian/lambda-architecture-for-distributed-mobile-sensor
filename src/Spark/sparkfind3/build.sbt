scalaVersion := "2.12.12"

// ============================================================================

val sparkVersion = "3.0.0"
val sparkName = "SparkFind3"

name := sparkName
organization := "de.cads.scala"
version := "0.1"
  
// ============================================================================

lazy val SparkFind3 = (project in file("."))
.settings(
    name := sparkName,
    libraryDependencies ++= Seq(
        "org.apache.spark" %% "spark-sql" % sparkVersion,
        "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
        "org.apache.spark" %% "spark-avro" % sparkVersion,
    )
)