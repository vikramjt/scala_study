package com.study.oops

abstract class MyListGeneric[+A] {
  /*
       head = first element of  the  list
       tail = remainder of the list
       isEmpty = is this list empty
       add(int) => new list with this element added
       toString => a string representation of the list
     */

  def head: A
  def tail: MyListGeneric[A]
  def isEmpty: Boolean
  def add[B >: A](element: B): MyListGeneric[B] //Need the supertype of A which will be B that will be added and returned the type of list.
  def printElements: String
  // polymorphic call
  override def toString: String = "[" + printElements + "]"
}

case object EmptyGeneric extends MyListGeneric[Nothing] {
  def head: Nothing = throw new NoSuchElementException
  def tail: MyListGeneric[Nothing] = throw new NoSuchElementException
  def isEmpty: Boolean = true
  def add[B >: Nothing](element: B): MyListGeneric[B] = new ConsGeneric(element, EmptyGeneric)
  def printElements: String = ""
}

case class ConsGeneric[+A](h: A, t: MyListGeneric[A]) extends MyListGeneric[A] { //As the MyList is covariant [+A] then the child should be covariant
  def head: A = h
  def tail: MyListGeneric[A] = t
  def isEmpty: Boolean = false
  def add[B >: A](element: B): MyListGeneric[B] = new ConsGeneric(element, this)
  def printElements: String =
    if (t.isEmpty) "" + h
    else h + " " + t.printElements
}

object ListTestGeneric extends App {
  val listOfIntegers: MyListGeneric[Int] = new ConsGeneric(1, new ConsGeneric(2, new ConsGeneric(3, EmptyGeneric)))
  println(listOfIntegers.toString)
  val listOfStrings: MyListGeneric[String] = new ConsGeneric("One", new ConsGeneric("Two", EmptyGeneric))
  println(listOfStrings.toString)
}