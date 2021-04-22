package org.example.datasource.postgres

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.catalog.{SupportsRead, SupportsWrite, Table, TableCapability, TableProvider}
import org.apache.spark.sql.connector.expressions.Transform
import org.apache.spark.sql.connector.read.{Batch, InputPartition, PartitionReader, PartitionReaderFactory, Scan, ScanBuilder}
import org.apache.spark.sql.connector.write.{BatchWrite, DataWriter, DataWriterFactory, LogicalWriteInfo, PhysicalWriteInfo, WriteBuilder, WriterCommitMessage}
import org.apache.spark.sql.types._
import org.apache.spark.sql.util.CaseInsensitiveStringMap

import java.sql.{DriverManager, ResultSet}
import java.util
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer


class DefaultSource extends TableProvider {
  override def inferSchema(options: CaseInsensitiveStringMap): StructType = PostgresTable.schema

  override def getTable(
                         schema: StructType,
                         partitioning: Array[Transform],
                         properties: util.Map[String, String]
                       ): Table = new PostgresTable(properties.get("tableName")) // TODO: Error handling
}

class PostgresTable(val name: String) extends SupportsRead with SupportsWrite {
  override def schema(): StructType = PostgresTable.schema

  override def capabilities(): util.Set[TableCapability] = Set(
    TableCapability.BATCH_READ,
    TableCapability.BATCH_WRITE
  ).asJava

  override def newScanBuilder(options: CaseInsensitiveStringMap): ScanBuilder = new PostgresScanBuilder(options)

  override def newWriteBuilder(info: LogicalWriteInfo): WriteBuilder = new PostgresWriteBuilder(info.options)
}

object PostgresTable {
  val schema: StructType = new StructType().add("user_id", LongType)
}

case class ConnectionProperties(url: String
                                , user: String
                                , password: String
                                , tableName: String
                                , partitionColumn: String
                                , partitionSize: Int)

/** Read */
class PostgresScanBuilder(options: CaseInsensitiveStringMap) extends ScanBuilder {
  override def build(): Scan = new PostgresScan(ConnectionProperties(
    options.get("url"),
    options.get("user"),
    options.get("password"),
    options.get("tableName"),
    options.get("partitionColumn"),
    options.get("partitionSize").toInt
  ))
}

object PostgresPartitionsList {
  private def getTableBoundParams(connectionProperties: ConnectionProperties): (Long, Long) = {
    val connection = DriverManager.getConnection(
      connectionProperties.url,
      connectionProperties.user,
      connectionProperties.password
    )

    val query =
      s"""
      SELECT
        min(${connectionProperties.partitionColumn}) as min,
        max(${connectionProperties.partitionColumn}) as max
      FROM ${connectionProperties.tableName}
    """

    val statement = connection.createStatement()

    val row = statement.executeQuery(query)

    row.next()

    val lowerBound = row.getLong(1)
    val upperBound = row.getLong(2)

    (lowerBound, upperBound)
  }

  def apply(connectionProperties: ConnectionProperties): Array[InputPartition] = {
    val (lowerBound, upperBound) = getTableBoundParams(connectionProperties)
    val partitionSize = connectionProperties.partitionSize

    val partitions = ArrayBuffer[PostgresPartition]()
    for (limit <- lowerBound.to(upperBound).by(partitionSize)) {
      val offset = limit + partitionSize - 1

      partitions +=
        new PostgresPartition(
          connectionProperties.url,
          connectionProperties.user,
          connectionProperties.password,
          connectionProperties.tableName,
          limit,
          offset
        )
    }

    partitions.toArray
  }
}

class PostgresPartition(val url: String,
                        val user: String,
                        val password: String,
                        val tableName: String,
                        val limit: Long,
                        val offset: Long) extends InputPartition

class PostgresScan(connectionProperties: ConnectionProperties) extends Scan with Batch {
  override def readSchema(): StructType = PostgresTable.schema

  override def toBatch: Batch = this

  override def planInputPartitions(): Array[InputPartition] = PostgresPartitionsList(connectionProperties)

  override def createReaderFactory(): PartitionReaderFactory = new PostgresPartitionReaderFactory(connectionProperties)
}

class PostgresPartitionReaderFactory(connectionProperties: ConnectionProperties)
  extends PartitionReaderFactory {
  override def createReader(partition: InputPartition): PartitionReader[InternalRow] = new PostgresPartitionReader(partition.asInstanceOf[PostgresPartition])
}

class PostgresPartitionReader(inputPartition: PostgresPartition) extends PartitionReader[InternalRow] {
  private val connection = DriverManager.getConnection(
    inputPartition.url, inputPartition.user, inputPartition.password
  )
  private val statement = connection.createStatement()

    val query = s"""
        SELECT *
        FROM ${inputPartition.tableName}
        LIMIT ${inputPartition.limit}
        OFFSET ${inputPartition.offset}
      """

  private val resultSet = statement.executeQuery(s"select * from ${inputPartition.tableName}")

  override def next(): Boolean = resultSet.next()

  override def get(): InternalRow = InternalRow(resultSet.getLong(1))

  override def close(): Unit = connection.close()
}

/** Write */
class PostgresWriteBuilder(options: CaseInsensitiveStringMap) extends WriteBuilder {
  override def buildForBatch(): BatchWrite = new PostgresBatchWrite(ConnectionProperties(
    options.get("url")
    , options.get("user")
    , options.get("password")
    , options.get("tableName")
    , options.get("partitionColumn")
    , options.get("partitionSize").toInt
  )

  )
}

class PostgresBatchWrite(connectionProperties: ConnectionProperties) extends BatchWrite {
  override def createBatchWriterFactory(physicalWriteInfo: PhysicalWriteInfo): DataWriterFactory =
    new PostgresDataWriterFactory(connectionProperties)

  override def commit(writerCommitMessages: Array[WriterCommitMessage]): Unit = {}

  override def abort(writerCommitMessages: Array[WriterCommitMessage]): Unit = {}
}

class PostgresDataWriterFactory(connectionProperties: ConnectionProperties) extends DataWriterFactory {
  override def createWriter(partitionId: Int, taskId: Long): DataWriter[InternalRow] =
    new PostgresWriter(connectionProperties)
}

object WriteSucceeded extends WriterCommitMessage

class PostgresWriter(connectionProperties: ConnectionProperties) extends DataWriter[InternalRow] {

  val connection = DriverManager.getConnection(
    connectionProperties.url,
    connectionProperties.user,
    connectionProperties.password
  )
  val statement = "insert into users (user_id) values (?)"
  val preparedStatement = connection.prepareStatement(statement)

  override def write(record: InternalRow): Unit = {
    val value = record.getLong(0)
    preparedStatement.setLong(1, value)
    preparedStatement.executeUpdate()
  }

  override def commit(): WriterCommitMessage = WriteSucceeded

  override def abort(): Unit = {}

  override def close(): Unit = connection.close()
}

