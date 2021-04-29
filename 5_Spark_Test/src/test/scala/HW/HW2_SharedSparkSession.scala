package HW

import HW.HW_task1.{processPopularBorough, readCSV, readParquet}
import HW.HW_task3.processDistanceDistribution
//import lesson2.OtusFragmentedByMethod.{processTaxiData, readCSV, readParquet}
import org.apache.spark.sql.QueryTest.checkAnswer
import org.apache.spark.sql.Row
import org.apache.spark.sql.test.SharedSparkSession


class HW2_SharedSparkSession extends SharedSparkSession {

  import testImplicits._

  //  test("join - join using") {
  //    val df = Seq(1, 2, 3).map(i => (i, i.toString)).toDF("int", "str")
  //    val df2 = Seq(1, 2, 3).map(i => (i, (i + 1).toString)).toDF("int", "str")
  //
  //    checkAnswer(
  //      df.join(df2, "int"),
  //      Row(1, "1", "2") :: Row(2, "2", "3") :: Row(3, "3", "5") :: Nil)
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

}

