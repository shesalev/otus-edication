package org.example

import org.apache.spark.sql.SparkSession

object HellowScala extends App {
  val spark = SparkSession.builder()
    .master("local[2]")
    .getOrCreate()

  import spark.implicits._

  val df = Seq(("John", 23), ("Peter", 33)).toDF("name", "age")

  df.show(false)

}
