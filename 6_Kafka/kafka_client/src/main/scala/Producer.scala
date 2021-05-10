package com.example.kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.commons.csv.{CSVFormat, CSVParser, CSVRecord}

import java.util.Properties
import java.io.Reader
import scala.io.Source
import org.json4s.jackson.JsonMethods.{compact, render}
import org.json4s.JsonDSL._

import scala.jdk.CollectionConverters._

object Producer {

  case class Book(name: String,
                  author: String,
                  userRating: String,
                  reviews: String,
                  price: String,
                  year: String,
                  genre: String)

  def getBookListFromResources(inputFile: String) = {

    def source: Reader = Source.fromResource(inputFile).reader()

    val records = CSVParser.parse(source, CSVFormat.DEFAULT.withHeader()).getRecords.asScala

    for (record <- records) yield Book(
      record.get("Name")
      , record.get("Author")
      , record.get("User Rating")
      , record.get("Reviews")
      , record.get("Price")
      , record.get("Year")
      , record.get("Genre")
    )
  }

  def sendMsg(topic:String) {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:29092")

    val inputFile = "bestsellers_with_categories-1801-9dc31f.csv"

    val producer = new KafkaProducer(props, new StringSerializer, new StringSerializer)

    val bookList = getBookListFromResources(inputFile)

    bookList.foreach {
      book =>
        val jsonMsg = ("name" -> book.name) ~
          ("author" -> book.author) ~
          ("userRating" -> book.userRating) ~
          ("reviews" -> book.reviews) ~
          ("price" -> book.price) ~
          ("year" -> book.year) ~
          ("genre" -> book.genre)

        val msg = compact(render(jsonMsg))

        producer.send(new ProducerRecord(topic, msg, msg))
    }

    println("messages sended")

    producer.close()
  }
}
