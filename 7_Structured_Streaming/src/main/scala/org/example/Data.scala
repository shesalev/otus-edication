package org.example

case class Data(
    sepal_length: Double,
    sepal_width: Double,
    petal_length: Double,
    petal_width: Double
)

object Data {
  def apply(a: Array[String]): Data =
    Data(
      a(0).toDouble,
      a(1).toDouble,
      a(2).toDouble,
      a(3).toDouble
    )
}
