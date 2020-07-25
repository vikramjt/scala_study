package com.study.oops

trait MyPredicate[T] {
  def test(obj: T): Boolean
}

trait MyTransform [A, B] {
  def transformAtoB(transform: A): B
}

class EvenPredicate extends MyPredicate[Int] {
  override def test(obj: Int): Boolean = (obj % 2 ==0)
}

class StringToIntTransform [String, Int] extends MyTransform [String, Int]{
  override def transformAtoB(transform: String): Int = Integer.getInteger(transform)
}

abstract class MyListPreTran[+A] {
  def head: A
  def tail: MyListPreTran[A]
  def isEmpty: Boolean
  def add[B >: A](element: B): MyListPreTran[B] //Need the supertype of A which will be B that will be added and returned the type of list.
  def printElements: String
  // polymorphic call
  override def toString: String = "[" + printElements + "]"
}

case object EmptyPreTran extends MyListPreTran[Nothing] {
  def head: Nothing = throw new NoSuchElementException
  def tail: MyListPreTran[Nothing] = throw new NoSuchElementException
  def isEmpty: Boolean = true
  def add[B >: Nothing](element: B): MyListPreTran[B] = new ConsPreTran(element, EmptyPreTran)
  def printElements: String = ""
}

case class ConsPreTran[+A](h: A, t: MyListPreTran[A]) extends MyListPreTran[A] { //As the MyList is covariant [+A] then the child should be covariant
  def head: A = h
  def tail: MyListPreTran[A] = t
  def isEmpty: Boolean = false
  def add[B >: A](element: B): MyListPreTran[B] = new ConsPreTran(element, this)
  def printElements: String =
    if (t.isEmpty) "" + h
    else h + " " + t.printElements
}

object ListTestPreTran extends App {
  val listOfIntegers: MyListPreTran[Int] = new ConsPreTran(1, new ConsPreTran(2, new ConsPreTran(3, EmptyPreTran)))
  println(listOfIntegers.toString)
  val listOfStrings: MyListPreTran[String] = new ConsPreTran("One", new ConsPreTran("Two", EmptyPreTran))
  println(listOfStrings.toString)
}