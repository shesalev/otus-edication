package example

import scala.io.Source
import java.io.FileOutputStream
import java.io.PrintStream

import io.circe._
import io.circe.parser._
import io.circe.syntax._
import io.circe.generic.auto._

// sbt run 'Daniil Medvedev' 1 out.csv
// sbt assembly
// java -jar 'target/scala-2.13/Scala Seed Project-assembly-0.1.0-SNAPSHOT.jar' 'Daniil Medvedev' 1 out.csv
// java -jar 'target/scala-2.13/json-parser-assembly-0.1.0-SNAPSHOT.jar' out.json


case class Country(region: String, name: String, area: Float, capital: List[String])

case class resultCountry(name: String, capital: String, area: Float)

object Country {

  implicit val decoder: Decoder[Country] = (hCursor: HCursor) =>
    for {
      region <- hCursor.get[String]("region")
      area <- hCursor.downField("area").as[Float]
      name <- hCursor.downField("name").get[String]("official")
      capital <- hCursor.get[List[String]]("capital")
    } yield Country(region, name, area, capital)

  def main(args: Array[String]): Unit = {

    def isValidMatch(regionName: String)(record: Country): Boolean = {
      regionName == record.region
    }

    def inputString = Source.fromURL(
      "https://raw.githubusercontent.com/mledoze/countries/master/countries.json"
    ).mkString

    val africaCountries: List[Country] = parser.decode[List[Country]](inputString) match {
      case Right(countries) => /*africaCountries ++=*/ countries.filter(isValidMatch("Africa"))
      case Left(ex) => {
        println(s"Oops something is wrong with decoding value ${ex}")
        List.empty[Country]
      }
    }

    def jsonPrinter[A](obj: A)(implicit encoder: Encoder[A]): String =
      obj.asJson.noSpaces

    def convertToResultCountry(country: Country): resultCountry = {
      resultCountry(country.name, country.capital.head, country.area)
    }

    def renderAllMatches(countries: List[Country]): List[resultCountry] = {
      countries
        .sortBy(_.area)(Ordering[Float].reverse)
        .take(10)
        .map(convertToResultCountry _)
    }

    val outputFile = args(0)
    val fos = new FileOutputStream(outputFile)
    val printer = new PrintStream(fos)

    if (africaCountries.size > 0) {
      printer.println(jsonPrinter(renderAllMatches(africaCountries)))
      println("The list of countries is written to the file: " + outputFile)
    } else {
      println("Error!!! The list of countries is empty")
    }

  }
}