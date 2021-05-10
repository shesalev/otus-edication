# Домашнее задание №3 «Чтение данных из Kafka»

## Задача

Написать приложение на Scala, которое будет выполнять следующее:
1. Вычитывать из CSV-файла, который можно скачать по [ссылке](https://www.kaggle.com/sootersaalu/amazon-top-50-bestselling-books-2009-2019), данные, сериализовывать их в JSON, и записывать в топик **books** локльно развернутого сервиса Apache Kafka.
2. Вычитать из топика books данные и распечатать в stdout последние 5 записей (c максимальным значением offset) из каждой партиции. При чтении топика одновременно можно хранить в памяти только 15 записей.

---

## Документация для запуска приложения

### Build & Prepare Kafka cluster
* Run command
```bash
docker-compose up -d
```
* Create new topic
```bash
docker exec 6_kafka_broker_1 kafka-topics \
  --create \
  --zookeeper zookeeper:2181 \
  --replication-factor 1 \
  --partitions 3 \
  --topic books
```

### Assembly JAR file for project:
* Run command
```bash
sbt assembly
```
* Get file `kafka-app-assembly-{VERSION}.jar` in directory `target/scala-2.12/`

### Run app
* Run command
```bash
java -jar kafka-app-assembly-{VERSION}.jar
```