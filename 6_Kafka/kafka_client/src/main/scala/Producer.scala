import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import java.util.Properties

object Producer extends App {

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:29092")

  val producer = new KafkaProducer(props, new StringSerializer, new StringSerializer)

  val messages = List(
    "message1",
    "message2",
    "message3",
  )

  messages.foreach { m =>
    producer.send(new ProducerRecord("mytopic", m, m))
  }

  producer.close()

}
