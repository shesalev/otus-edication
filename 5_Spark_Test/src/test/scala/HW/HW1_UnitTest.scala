package HW

import HW.HW_task2.{processPopularTime,readParquet}
//import lesson2.OtusFragmentedByMethod.{processTaxiData, readCSV, readParquet}
import org.apache.spark.sql.SparkSession
import org.scalatest.flatspec.AnyFlatSpec


class HW1_UnitTest extends AnyFlatSpec {

  implicit val spark = SparkSession.builder()
    .config("spark.master", "local")
    .appName("Test №1 for Big Data Application")
    .getOrCreate()

  it should "upload and process data" in {
    val taxiFactsDF = readParquet("src/main/resources/data/yellow_taxi_jan_25_2018")

    val popularTimeRDD = processPopularTime(taxiFactsDF).collect().head

    assert(popularTimeRDD._1.toString == "09:51:56")
    assert(popularTimeRDD._2 == 18)
  }

}
