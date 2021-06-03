# Домашнее задание

#### Настроите применение предобученной модели в Spark Structured Streaming

**Цель:** 

Научимся обучать и сохранять модели Spark ML. Научимся использовать предобученные модели Spark ML в Spark Structured Streaming

- Построить модель классификации Ирисов Фишера и сохранить её Описание набора данных: https://ru.wikipedia.org/wiki/%D0%98%D1%80%D0%B8%D1%81%D1%8B_%D0%A4%D0%B8%D1%88%D0%B5%D1%80%D0%B0 Набор данных в формате CSV: https://www.kaggle.com/arshid/iris-flower-dataset Набор данных в формате LIBSVM: https://github.com/apache/spark/blob/master/data/mllib/iris_libsvm.txt Должен быть предоставлен код построения модели (ноутбук или программа)
- Разработать приложение, которое читает из одной темы Kafka (например, "input") CSV-записи с четырми признаками ирисов, и возвращает в другую тему (например, "predictition") CSV-записи с теми же признаками и классом ириса
- Предоставить снимки экрана с записями в обеих темах Kafka

### create kafka topics
docker exec 7_structured_streaming_broker_1 kafka-topics \
  --create \
  --zookeeper zookeeper:2181 \
  --replication-factor 1 \
  --partitions 3 \
  --topic input

docker exec 7_structured_streaming_broker_1 kafka-topics \
--create \
  --zookeeper zookeeper:2181 \
  --replication-factor 1 \
  --partitions 3 \
  --topic prediction

### 1. Learn model
org.example.Model

### 2. Use model
org.example.MLStructuredStreaming

### 3. Screenshot
Screenshot.png