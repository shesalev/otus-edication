package HW

import HW.HW_task1.{processPopularBorough, readCSV, readParquet}
import HW.HW_task3.processDistanceDistribution
//import lesson2.OtusFragmentedByMethod.{processTaxiData, readCSV, readParquet}
import org.apache.spark.sql.QueryTest.checkAnswer
import org.apache.spark.sql.Row
import org.apache.spark.sql.test.SharedSparkSession


class DF_DS_SharedSparkSession extends SharedSparkSession {

  import testImplicits._

  //  test("join - join using") {
  //    val df = Seq(1, 2, 3).map(i => (i, i.toString)).toDF("int", "str")
  //    val df2 = Seq(1, 2, 3).map(i => (i, (i + 1).toString)).toDF("int", "str")
  //
  //    checkAnswer(
  //      df.join(df2, "int"),
  //      Row(1, "1", "2") :: Row(2, "2", "3") :: Row(3, "3", "5") :: Nil)
  //  }

  //  test("join - processTaxiData") {
  //    val taxiZonesDF2 = readCSV("src/main/resources/data/taxi_zones.csv")
  //    val taxiDF2 = readParquet("src/main/resources/data/yellow_taxi_jan_25_2018")
  //
  //    val actualDistribution = processTaxiData(taxiZonesDF2, taxiDF2)
  //
  //    checkAnswer(
  //      actualDistribution,
  //      Row("Manhattan", 304266, 0.0, 2.23, 66.0) ::
  //        Row("Queens", 17712, 0.0, 11.14, 53.5) ::
  //        Row("Unknown", 6644, 0.0, 2.34, 42.8) ::
  //        Row("Brooklyn", 3037, 0.0, 3.28, 27.37) ::
  //        Row("Bronx", 211, 0.0, 2.99, 20.09) ::
  //        Row("EWR", 19, 0.0, 3.46, 17.3) ::
  //        Row("Staten Island", 4, 0.0, 0.2, 0.5) :: Nil
  //    )
  //
  //  }

  test("join - processPopularBorough") {
    val taxiFactsDF = readParquet("src/main/resources/data/yellow_taxi_jan_25_2018")

    val taxiZoneDF = readCSV("src/main/resources/data/taxi_zones.csv")

    val resultDf = processPopularBorough(taxiFactsDF, taxiZoneDF)

    checkAnswer(
      resultDf,
      Row("Manhattan", 295642) ::
        Row("Queens", 13394) ::
        Row("Brooklyn", 12587) ::
        Row("Unknown", 6285) ::
        Row("Bronx", 1562) ::
        Row("EWR", 491) ::
        Row("Staten Island", 62) :: Nil
    )

  }

  test("join - processDistanceDistribution") {
    val taxiFactsDF = readParquet("src/main/resources/data/yellow_taxi_jan_25_2018")
    val taxiZoneDF = readCSV("src/main/resources/data/taxi_zones.csv")

    val distanceDistribution = processDistanceDistribution(taxiFactsDF, taxiZoneDF)

    checkAnswer(
      distanceDistribution,
      Row("Manhattan", 295642, 2.19, 2.63, 0.01, 37.92) ::
        Row("Queens", 13394, 8.99, 5.42, 0.01, 51.6) ::
        Row("Brooklyn", 12587, 6.93, 4.75, 0.01, 44.8) ::
        Row("Unknown", 6285, 3.69, 5.72, 0.01, 66.0) ::
        Row("Bronx", 1562, 9.21, 5.33, 0.02, 31.18) ::
        Row("EWR", 491, 17.56, 3.76, 0.01, 45.98) ::
        Row("Staten Island", 62, 20.11, 6.89, 0.3, 33.78) :: Nil
    )

  }

}

