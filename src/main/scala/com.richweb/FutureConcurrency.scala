package com.richweb

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import scala.util.{Failure, Success}

import java.util.concurrent.Executors
import _root_.scala.util.Random

object FutureConcurrency {

  //val ec = ExecutionContext.global
  val ex = Executors.newFixedThreadPool(4)
  implicit val ec = ExecutionContext.fromExecutor(ex)

  def timesTwo(n: Int): Future[Int] = {
    Future {
      println(s"Running on ${Thread.currentThread.getName()}")
      Thread.sleep(2000)
      n * 2
    }(ec)
  }

  def timesFour(n: Int): Future[Int] = {
    //implicit val e = ec

    for {
      i <- timesTwo(n)
      j <- timesTwo(n)
    } yield i + j
  }

  def timesFourPar(n: Int): Future[Int] = {
    //implicit val e = ec
    val f1 = timesTwo(n)
    val f2 = timesTwo(n)

    println("time to join")

    for {
      i <- f1
      j <- f2
    } yield i + j
  }

  def main(args: Array[String]): Unit = {
    val f = timesTwo(2)
    val r = Await.result(f, 2.seconds)
    println(s"Result on ${Thread.currentThread.getName()} is $r")

    val f1 = timesTwo(10)
    println("Trying on complete")
    f1.onComplete {
      case Success(i) => println(i)
      case Failure(t) => println(t.getMessage())
    }(ec)

    Thread.sleep(2000)

    val f2 = timesFour(4)
    println(Await.result(f2, 4.seconds))

    val f3 = timesFourPar(4)
    println(Await.result(f3, 2100.milliseconds)) // try with 2.11.12

    //implicit val e = ec
    val lst = List("Hello", "Beautiful", "World")
    val lstFut = lst.map(s => Future {
      println(s"Result on ${Thread.currentThread.getName()} is $r")
      Thread.sleep(Random.nextInt(3))
      s
    })

    val futLst = Future.sequence(lstFut)
    val l = Await.result(futLst, 1.second)
    println(l.mkString(" "))

    ex.shutdown()
  }
}
