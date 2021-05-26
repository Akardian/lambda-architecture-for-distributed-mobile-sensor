scalaVersion := "2.12.12"

// ============================================================================

val sparkVersion = "3.0.0"
val projectName = "sparkFind3Batch"
val projectVersion = "0.2"

name := projectName
organization := "de.cads.scala"
version := projectVersion
  
// ============================================================================

libraryDependencies ++= Seq(
            "org.apache.spark" %% "spark-sql" % sparkVersion,
            "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
            "org.apache.spark" %% "spark-avro" % sparkVersion
        )

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
assemblyJarName in assembly := (projectName + "-" + projectVersion + ".jar")

assemblyMergeStrategy in assembly := {
    case "reference.conf" => MergeStrategy.concat
    case "META-INF/services/org.apache.spark.sql.sources.DataSourceRegister" => MergeStrategy.concat
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
}