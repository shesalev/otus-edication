package HW

/*
Загрузить данные в DataSet из файла с фактическими данными поездок в Parquet (src/main/resources/data/yellow_taxi_jan_25_2018). С помощью DSL и lambda построить таблицу, которая покажет. Как происходит распределение поездок по дистанции? Результат вывести на экран и записать в бд Постгрес (докер в проекте). Для записи в базу данных необходимо продумать и также приложить инит sql файл со структурой.
*/

import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, count, max, mean, min, stddev, round, broadcast}

import java.util.Properties

case class TaxiZoneTripDF(
                           borough: String,
                           trip_distance: Double
                         )

object HW_task3 extends App {

  def saveToDB(ds: Dataset[Row], tableName: String): Unit = {
    val driver = "org.postgresql.Driver"
    val url = "jdbc:postgresql://localhost:5432/otus"
    val user = "otus"
    val password = "otus"

    try {
      val connectionProperties = new Properties()
      connectionProperties.put("user", user)
      connectionProperties.put("password", password)
      connectionProperties.put("driver", driver)

      ds
        .write
        .mode(SaveMode.Overwrite)
        .jdbc(url, tableName, connectionProperties)
      println(s"Writing to the file on path '$tableName' was successful")
    } catch {
      case _: Throwable => println("Got some other kind of Throwable exception")
    }

  }

  def readCSV(path: String)(implicit spark: SparkSession): DataFrame =
    spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(path)

  def readParquet(path: String)(implicit spark: SparkSession): DataFrame = spark.read.load(path)

  def processDistanceDistribution(taxiInfoDF: DataFrame, taxiDictDF: DataFrame): Dataset[Row] = {
    import spark.implicits._

    taxiInfoDF.select("DOLocationID", "trip_distance")
      .join(broadcast(taxiDictDF), taxiInfoDF("DOLocationID") === taxiDictDF("LocationID"))
      .select(col("Borough"), col("trip_distance")).as[TaxiZoneTripDF]
      .filter(tz => tz.trip_distance > 0)
      .groupBy(taxiDictDF("Borough") as "borough")
      .agg(
        count(taxiDictDF("Borough")) as "count",
        round(mean(taxiInfoDF("trip_distance")), 2) as "mean_distance",
        round(stddev(taxiInfoDF("trip_distance")), 2) as "std_distance",
        round(min(taxiInfoDF("trip_distance")), 2) as "min_distance",
        round(max(taxiInfoDF("trip_distance")), 2) as "max_distance",
      )
      .sort(col("count").desc)
  }

  implicit val spark = SparkSession.builder()
    .appName("Introduction to RDDs")
    .config("spark.master", "local")
    .getOrCreate()

  val countOutRow = 10
  val tableName = "distance_distribution"

  val taxiFactsDF = readParquet("src/main/resources/data/yellow_taxi_jan_25_2018")
  val taxiZoneDF = readCSV("src/main/resources/data/taxi_zones.csv")

  val distanceDistribution = processDistanceDistribution(taxiFactsDF, taxiZoneDF)

  distanceDistribution.show(countOutRow)

  saveToDB(distanceDistribution, tableName)

  spark.stop()
}
