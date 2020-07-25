package com.study.oops

trait MyPredicate[-T] {
  def test(obj: T): Boolean
}

trait MyTransformer [-A, B] {
  def transform(elem: A): B
}

/*class EvenPredicate extends MyPredicate[Int] {
  override def test(obj: Int): Boolean = (obj % 2 ==0)
}

class StringToIntTransformer [String, Int] extends MyTransformer [String, Int]{
  override def transform(transform: String): Int = ???
}*/

abstract class MyListPreTran[+A] {
  def head: A
  def tail: MyListPreTran[A]
  def isEmpty: Boolean
  def add[B >: A](element: B): MyListPreTran[B] //Need the supertype of A which will be B that will be added and returned the type of list.

  def printElements: String
  // polymorphic call
  override def toString: String = "[" + printElements + "]"

  def map[B](myTransform: MyTransformer[A, B]): MyListPreTran[B]
  def filter(predicate: MyPredicate[A]): MyListPreTran[A]

  def ++[B >: A](anotherMyList: MyListPreTran[B]) : MyListPreTran[B]
  def flatMap[B](myTransformer: MyTransformer[A, MyListPreTran[B]]): MyListPreTran[B]
}

case object EmptyPreTran extends MyListPreTran[Nothing] {
  def head: Nothing = throw new NoSuchElementException
  def tail: MyListPreTran[Nothing] = throw new NoSuchElementException
  def isEmpty: Boolean = true
  def add[B >: Nothing](element: B): MyListPreTran[B] = new ConsPreTran(element, EmptyPreTran)
  def printElements: String = ""

  override def map[B](myTransform: MyTransformer[Nothing, B]): MyListPreTran[B] = EmptyPreTran
  override def filter(predicate: MyPredicate[Nothing]): MyListPreTran[Nothing] = EmptyPreTran

  override def ++[B >: Nothing](anotherMyList: MyListPreTran[B]) : MyListPreTran[B] = anotherMyList
  override def flatMap[B](myTransformer: MyTransformer[Nothing, MyListPreTran[B]]): MyListPreTran[B] = EmptyPreTran
}

case class ConsPreTran[+A](h: A, t: MyListPreTran[A]) extends MyListPreTran[A] { //As the MyList is covariant [+A] then the child should be covariant
  def head: A = h
  def tail: MyListPreTran[A] = t
  def isEmpty: Boolean = false
  def add[B >: A](element: B): MyListPreTran[B] = new ConsPreTran(element, this)
  def printElements: String =
    if (t.isEmpty) "" + h
    else h + " " + t.printElements

  override def map[B](myTransform: MyTransformer[A, B]): MyListPreTran[B] =
    new ConsPreTran(myTransform.transform(h), t.map(myTransform))

  override def filter(predicate: MyPredicate[A]): MyListPreTran[A] =
    if(predicate.test(h)) new ConsPreTran(h, t.filter(predicate))
    else t.filter(predicate)

  override def ++[B >: A](anotherMyList: MyListPreTran[B]) : MyListPreTran[B] = new ConsPreTran(h, t ++ anotherMyList)

  override def flatMap[B](myTransformer: MyTransformer[A, MyListPreTran[B]]): MyListPreTran[B] =
      myTransformer.transform(h) ++ t.flatMap(myTransformer)
}

object MyListTestWithPredicateTransform extends App {
  val listOfIntegers: MyListPreTran[Int] = new ConsPreTran(1, new ConsPreTran(2, new ConsPreTran(3, new ConsPreTran(4, EmptyPreTran))))
  val anotherListOfIntegers: MyListPreTran[Int] = new ConsPreTran(5, new ConsPreTran(6, EmptyPreTran))
  println(listOfIntegers.toString)
  val listOfStrings: MyListPreTran[String] = new ConsPreTran("One", new ConsPreTran("Two", EmptyPreTran))
  println(listOfStrings.toString)

  println(listOfIntegers.map(new MyTransformer[Int, Int] {
    override def transform(elem: Int): Int = elem * 2
  }))
  // can be converted above to as
  // println(listOfIntegers.map((elem: Int) => elem * 2))

  println(listOfIntegers.filter(new MyPredicate[Int] {
    override def test(obj: Int): Boolean = obj % 2 == 0
  }))
  // can be converted above to as
  // println(listOfIntegers.filter((obj: Int) => obj % 2 == 0)))

  println((listOfIntegers ++ anotherListOfIntegers).toString)

  println(listOfIntegers.flatMap(new MyTransformer[Int, MyListPreTran[Int]] {
    override def transform(elem: Int): MyListPreTran[Int] = new ConsPreTran(elem, new ConsPreTran(elem+1, EmptyPreTran))
  }))

  //By adding the case keyword to the Empty object and MyList class the equal and hashcode method
  // are by default added to the classes and object.
  val cloneListOfIntegers: MyListPreTran[Int] = new ConsPreTran(1, new ConsPreTran(2, new ConsPreTran(3, new ConsPreTran(4, EmptyPreTran))))
  println(listOfIntegers == cloneListOfIntegers)
}