name := "spark_ml_streaming"

version := "0.1"

scalaVersion := "2.12.12"

lazy val sparkVersion = "3.1.1"
lazy val kafkaVersion = "2.7.0"

libraryDependencies ++= Seq(
  "com.typesafe"      % "config"      % "1.4.0",
  "org.apache.spark" %% "spark-sql"   % sparkVersion % Provided,
  "org.apache.spark" %% "spark-mllib" % sparkVersion % Provided,
  "org.apache.spark"  % "spark-sql-kafka-0-10_2.12" % sparkVersion,
  "org.apache.kafka"  % "kafka-clients"             % kafkaVersion
)

//lazy val sparkVersion = "3.1.1"
//lazy val kafkaVersion = "2.7.0"
//
//libraryDependencies ++= Seq(
//  "com.typesafe"      % "config"                    % "1.4.0",
//  "org.apache.spark" %% "spark-sql"                 % sparkVersion % "provided",
//  "org.apache.spark" %% "spark-mllib"               % sparkVersion % "provided",
//  "org.apache.spark"  % "spark-sql-kafka-0-10_2.12" % sparkVersion,
//  "org.apache.kafka"  % "kafka-clients"             % kafkaVersion
//)
