//name := "kafka_client"
//
//version := "0.1"
//
//scalaVersion := "2.13.4"
//
//libraryDependencies ++= Seq(
//  "org.apache.kafka" % "kafka-clients" % "2.6.0",
//  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.0",
//  "ch.qos.logback" % "logback-classic" % "1.2.3",
//  "org.apache.commons" % "commons-csv" % "1.8",
//  "org.json4s" %% "json4s-jackson" % "3.6.0",
//  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.0"
//)

lazy val _version: String = scala.io.Source
  .fromFile("VERSION")
  .getLines
  .toList.head.takeWhile(_ != ';').trim

lazy val mainSettings = Seq(
  name := "kafka-app",
  version := _version,
  organization := "com.exemple",
  scalaVersion := "2.13.4"
)

lazy val parser = (project in file(".")).
  settings(mainSettings: _*).
  settings {
    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-csv" % "1.8",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "org.json4s" %% "json4s-jackson" % "3.6.6",
      "org.apache.kafka" % "kafka-clients" % "2.6.0",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.0",
    )
  }
