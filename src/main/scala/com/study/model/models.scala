package com.study.model

import java.nio.file.Path
import java.time.{Duration, LocalDateTime}

sealed trait CommonConfiguration

case class KerberosConfig(principal: String, keytab: Path) extends CommonConfiguration

sealed trait Connection {
  val id: Int
  val name: String
}

//TODO: do not export password property
case class RdbmsConnection(id: Int, name: String, url: String, driverId: String, username: String, password: String)
    extends Connection {
  override def toString: String = s"RdbmsConnection($id, $name, $url, $driverId, ******, ******)"
}

case class HiveConnection(
  id: Int,
  name: String,
  url: String,
  username: Option[String],
  password: Option[String],
  hadoopResources: HadoopConfigResources,
  kerberos: Option[KerberosConfig] = None
) extends Connection {
  override def toString: String = s"HiveConnection($id, $name, $url, ******, ******, $hadoopResources, $kerberos)"
}

case class HdfsConnection(id: Int, name: String, kerberos: Option[KerberosConfig] = None) extends Connection

sealed trait Source {
  val id: Int
  val name: Option[String]
  val connectionId: Int
  val columns: List[Column]
}

sealed trait LoadStrategy

case class Full(
  columnName: Option[String],
  columnType: Option[String],
  maxValue: Option[String],
  minValue: Option[String]
) extends LoadStrategy

case class Incremental(columnName: String, initialMaxValue: Option[String]) extends LoadStrategy

case class Column(
  name: String,
  `type`: String,
  nullable: Boolean = false,
  size: Option[Int],
  decimalDigit: Option[Int],
  partitioned: Boolean = false,
  partitionValue: Option[String])

case class RdbmsSource(
  id: Int,
  name: Option[String],
  connectionId: Int,
  table: String,
  loadStrategy: LoadStrategy,
  columns: List[Column],
  customQuery: Option[String] = None
) extends Source

case class HiveSource(
  id: Int,
  name: Option[String],
  connectionId: Int,
  table: String,
  columns: List[Column],
  customQuery: Option[String] = None
) extends Source

case class HadoopConfigResources(files: List[Path])

case class HdfsSource(
  id: Int,
  name: Option[String],
  connectionId: Int,
  hadoopResources: HadoopConfigResources,
  directory: Path,
  columns: List[Column],
  keepSourceFile: Boolean = false,
  firstLineHeader: Boolean = false,
  fileFormat: DataFileFormat
) extends Source

sealed trait DataFileFormat

case object Csv extends DataFileFormat

case object Json extends DataFileFormat

case object Xml extends DataFileFormat

case object AvroFormat extends DataFileFormat

sealed trait Destination {
  val id: Int
  val name: Option[String]
  val connectionId: Int
  val inputNode: Int
}

case class RdbmsDestination(id: Int, name: Option[String], connectionId: Int, inputNode: Int, table: String)
    extends Destination

case class HdfsDestination(
  id: Int,
  name: Option[String],
  connectionId: Int,
  hadoopResources: HadoopConfigResources,
  inputNode: Int,
  directory: Path
) extends Destination

sealed trait HiveStorageFormat

case object Orc extends HiveStorageFormat

case object Avro extends HiveStorageFormat

case object Parquet extends HiveStorageFormat

case object Text extends HiveStorageFormat

sealed trait HivePartition

case object StaticPartition extends HivePartition

case object DynamicPartition extends HivePartition

case class HiveDestination(
  id: Int,
  name: Option[String],
  connectionId: Int,
  inputNode: Int,
  table: String,
  storage: HiveStorageFormat,
  partition: Option[HivePartition]
) extends Destination

sealed trait Schedule

case class Cron(expression: String) extends Schedule

case class Time(delay: Duration) extends Schedule

sealed trait Transformation {
  val id: Int

  def inputNode: Int
}

case class Union(id: Int, inputNodes: List[Int]) extends Transformation {
  override def inputNode: Int = inputNodes.headOption.getOrElse(0)
}

case class JoinColumns(left: String, right: String)

case class Join(id: Int, leftNode: Int, rightNode: Int, columns: List[JoinColumns]) extends Transformation {
  override def inputNode: Int = leftNode
}

sealed trait ColumnTransformation

case class NameChange(from: String, to: String) extends ColumnTransformation

case class ColumnConcatenation(from: List[String], to: String) extends ColumnTransformation

case class Schema(name: String, columns: List[Column])

case class ColumnMapping(id: Int, inputNode: Int, list: List[ColumnTransformation], outputSchema: Schema)
    extends Transformation

case class FromToColumn(from: String, to: String)

case class Pipeline(
  name: String,
  description: String,
  sources: List[Source],
  transformation: List[Transformation],
  destinations: List[Destination],
  schedule: Schedule
)

case class Template(created: LocalDateTime, pipeline: Pipeline, connections: List[Connection])
