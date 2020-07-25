package com.study.array

import com.study.model.Column

object ArrayUtil extends App {

  /*val list = List("a", "b", "c")
  println(list)

  val zwi = list.zipWithIndex
  println(zwi)*/

  val columns = List(
    Column("subscriber_id", "long", true, Some(10),Some(0), partitionValue = None),
    Column("created", "timestamp", true, Some(10),Some(0), true, Some("2020-04-23")),
    Column("created_day", "string", true, Some(10),Some(0), partitionValue = None),
    Column("duration", "long", true, Some(10),Some(0), true, Some("342342"))
  )

  val partitionAttrList = columns.map{ c =>
    if(c.partitioned){s"${c.name}=${c.partitionValue}"}
    else None
    //Some(c.partitioned).fold(None) {s"${c.name}=${c.partitionValue}"}
  }
  println(partitionAttrList)

  val partitionAttrMap = partitionAttrList.zipWithIndex.map{
    case(Some(part), i) => Map(s"partition_value$i" -> part)
    case _ => None
  }
  println(partitionAttrMap)
}
