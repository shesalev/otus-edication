# Домашнее задание №2 «Работа с файлами на HDFS»

## Задача

Написать приложение на Scala, которое будет выполнять следующее:

Очищать данные из папки `/stage` и складывать их в папку `/ods` в корне HDFS по следующим правилам:

1. Структура партиций (папок вида date=...) должна сохраниться.
2. Внутри папок должен остаться только один файл, содержащий все данные файлов из соответствующей партиции в папке `/stage`.

То есть, если у нас есть папка `/stage/date=2020-11-11` с файлами
``` text
part-0000.csv
part-0001.csv
```
то должна получиться папка `/ods/date=2020-11-11` с одним файлом
``` text
part-0000.csv
```
содержащим все данные из файлов папки `/stage/date=2020-11-11`.

**При выполнении нельзя использовать Spark, даже если вы уже с ним знакомы ;)**

---

## Документация для запуска приложения

### Build & Prepare Hadoop cluster
* Run command
```bash
docker-compose up -d
```
* Fix `/etc/hosts`
```text
127.0.0.1 namenode
127.0.0.1 datanode
```
* Add sample dataset
```bash
docker exec namenode hdfs dfs -put /sample_data/stage /
```
* Check sample dataset in hdfs
```bash
docker exec namenode hdfs dfs -ls /stage
```

### Assembly JAR file for project:
* Run command
```bash
sbt assembly
```
* Get file `hdfs-app-assembly-{VERSION}.jar` in directory `target/scala-2.12/`

### Run app
* Run command
```bash
java -jar hdfs-app-assembly-{VERSION}.jar
```