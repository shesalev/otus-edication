lazy val _version: String = scala.io.Source
  .fromFile("VERSION")
  .getLines
  .toList.head.takeWhile(_ != ';').trim

lazy val mainSettings = Seq(
  name := "hdfs-app",
  version := _version,
  organization := "com.example",
  scalaVersion := "2.12.8"
)

lazy val parser = (project in file(".")).
  settings(mainSettings: _*).
  settings {
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
      "org.apache.hadoop" % "hadoop-client" % "3.2.1"
    )
  }