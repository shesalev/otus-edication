import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import scala.jdk.CollectionConverters._

import java.util.Properties
import java.time.Duration

object Consumer extends App {

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:29092")
  props.put("group.id", "consumer1")

  val consumer = new KafkaConsumer(props, new StringDeserializer, new StringDeserializer)

  consumer.subscribe(List("mytopic").asJavaCollection)

  consumer
    .poll(Duration.ofSeconds(1))
    .asScala
    .foreach { r => println(r.value()) }

  consumer.close()

}
