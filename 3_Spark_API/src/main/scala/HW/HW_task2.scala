package HW

/*
Загрузить данные в RDD из файла с фактическими данными поездок в Parquet (src/main/resources/data/yellow_taxi_jan_25_2018). С помощью lambda построить таблицу, которая покажет В какое время происходит больше всего вызовов. Результат вывести на экран и в txt файл c пробелами.
*/

//import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, LocalTime}

object HW_task2 extends App {
  implicit val spark = SparkSession.builder()
    .appName("Introduction to RDDs")
    .config("spark.master", "local")
    .getOrCreate()

  val context = spark.sparkContext

  val countOutRow = 10
  val outPath = "out/PopularTime"

  def readParquet(path: String)(implicit spark: SparkSession): DataFrame = spark.read.load(path)

  def writeTxt(rdd: RDD[(LocalTime, Int)], path: String)(implicit spark: SparkSession): Unit = {
    try {
      rdd.repartition(1)
        .map(row => row._1.toString + " " + row._2.toString)
        .saveAsTextFile(outPath)
      println(s"Writing to the file on path '$path' was successful")
    } catch {
      case _: Throwable => println("Got some other kind of Throwable exception")
    }
  }

  private def prepareStamp(stamp: String): LocalTime = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
    LocalDateTime.parse(stamp, formatter).toLocalTime
  }

  def processPopularTime(taxiInfoDF: DataFrame): RDD[(LocalTime, Int)] = {
    taxiInfoDF.rdd
      .map(trip => (prepareStamp(trip(1).toString), 1))
      .reduceByKey(_ + _)
      .sortBy(_._2, ascending = false)
  }

  val taxiFactsDF = readParquet("src/main/resources/data/yellow_taxi_jan_25_2018")

  val popularTimeRDD = processPopularTime(taxiFactsDF)

  popularTimeRDD.take(countOutRow).foreach(println)

  writeTxt(popularTimeRDD, outPath)

  spark.stop()
}