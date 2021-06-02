name := "spark_ml"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "2.4.8" % Provided,
  "com.github.pureconfig" %% "pureconfig" % "0.14.0"
)
