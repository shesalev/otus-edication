package com.example.kafka

import com.typesafe.scalalogging.LazyLogging

object main extends LazyLogging {
  val topic = "books"

  def main(args: Array[String]): Unit = {
    logger.info("Start app")

    Producer.sendMsg(topic)
    Consumer.get(topic)

    logger.info("Stop app")
  }
}