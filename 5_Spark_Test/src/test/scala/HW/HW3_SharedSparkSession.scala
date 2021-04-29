package HW

import HW.HW_task1.{processPopularBorough, readCSV, readParquet}
import HW.HW_task3.processDistanceDistribution
//import lesson2.OtusFragmentedByMethod.{processTaxiData, readCSV, readParquet}
import org.apache.spark.sql.QueryTest.checkAnswer
import org.apache.spark.sql.Row
import org.apache.spark.sql.test.SharedSparkSession


class HW3_SharedSparkSession extends SharedSparkSession {

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

