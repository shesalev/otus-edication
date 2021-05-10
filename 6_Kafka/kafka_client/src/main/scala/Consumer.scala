package com.example.kafka

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.TopicPartition
import scala.jdk.CollectionConverters._

import java.util
import java.util.Properties
import java.time.Duration

object Consumer {

  def get(topic: String) {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:29092")
    props.put("group.id", "consumer1")

    val cntLastMsg = 5

    val consumer = new KafkaConsumer(props, new StringDeserializer, new StringDeserializer)

    consumer.subscribe(List("books").asJavaCollection)

    val partitions = consumer.partitionsFor(topic).asScala
    val topicList = new util.ArrayList[TopicPartition]()

    partitions.foreach {
      p =>
        topicList.add(new TopicPartition(p.topic(), p.partition()))
    }

    val messages = consumer.poll(Duration.ofSeconds(1))

    consumer.seekToEnd(topicList)

    val partitionOffset = new util.HashMap[String, Long]()
    for (partition <- topicList.asScala) {
      partitionOffset.put(partition.toString, consumer.position(partition) - 1)
    }

    println(partitionOffset)

    val buf = new util.HashMap[String, List[String]]()
    for (msg <- messages.asScala) {
      val key = s"${msg.topic()}-${msg.partition()}"

      if (msg.offset() >= partitionOffset.get(key) - cntLastMsg && msg.offset() < partitionOffset.get(key)) {
        val msgWithOffset = s"offset: ${msg.offset()} | msg: ${msg.value()}"
        if (!buf.containsKey(key)) {
          buf.put(key, List(msgWithOffset))
        } else {
          val currentMsgList = buf.get(key)
          if (currentMsgList.length <= cntLastMsg) {
            val newMsgList = msgWithOffset :: currentMsgList
            buf.put(key, newMsgList)
          }
        }
      }
    }

    for (partition <- topicList.asScala) {
      val key = partition.toString
      println("--------------")
      println(s"Topic: $key")
      println("--------------")
      buf.getOrDefault(key, List()).foreach(println)
    }

    consumer.close()
  }


}
