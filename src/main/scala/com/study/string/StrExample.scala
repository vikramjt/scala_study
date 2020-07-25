package com.study.string

object StrExample extends App{

  val dateVal = "2020-07-21"
  val intVal = "3"
  println(s"${getValue(dateVal, "datetime")} and ${getValue(intVal, "int")}")

  private def getValue(value: String, columnType: String) = columnType.toLowerCase match {
    case t if isDatelikeType(t) ⇒ s"""'$value'"""
    case _ ⇒ value
  }

  def isDatelikeType(`type`: String): Boolean = `type`.toLowerCase match {
    case "timestamp" | "datetime" | "date" => true
    case _ => false
  }
}
