package org.example

import com.typesafe.config.ConfigFactory
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object MLStructuredStreaming {
  def main(args: Array[String]): Unit = {

    val config                 = ConfigFactory.load()
    val inputBootstrapServers  = config.getString("input.bootstrap.servers")
    val inputTopic             = config.getString("input.topic")
    val outputBootstrapServers = config.getString("output.bootstrap.servers")
    val outputTopic            = config.getString("output.topic")
    val modelPath              = config.getString("modelPath")
    val checkpointLocation     = config.getString("checkpointLocation")

    val spark = SparkSession
      .builder()
      .appName("MLStructuredStreaming")
      .config("spark.master", "local")
      .getOrCreate()

    val model = PipelineModel.load(modelPath)

    import spark.implicits._

    val input = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", inputBootstrapServers)
      .option("subscribe", inputTopic)
      .load()
      .selectExpr("CAST(value as STRING)")
      .as[String]
      .map(_.replace("\"", "").split(","))
      .map(Data(_))

    val prediction = model.transform(input)

    val query = prediction
      .select(
        concat_ws(
          ",",
          $"sepal_length",
          $"sepal_width",
          $"petal_length",
          $"petal_width",
          $"predictedLabel"
        ).as("value")
      )
      .writeStream
      .option("checkpointLocation", checkpointLocation)
      .outputMode("append")
      .format("kafka")
      .option("kafka.bootstrap.servers", outputBootstrapServers)
      .option("topic", outputTopic)
      .start()

    query.awaitTermination()

    spark.stop()
  }
}
