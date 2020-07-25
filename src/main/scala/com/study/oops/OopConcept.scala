package com.study.oops

object OopConcept extends App{

  class Person(name: String, age: Int = 0){
      def +(value: String): Person = new Person(value)

      def unary_+(): Unit = println(s"${this.name} with age : ${age + 1}")

      def print(): Unit = println(s"Name of Person : ${this.name} and Age: ${age}")

      def apply(noMov: Int): Unit = println(s"${this.name} watched the Inception $noMov times")
  }

  val vikram = new Person("Vikram")
  +vikram
  (vikram + "Rockstart").print()
  vikram(2)


}
