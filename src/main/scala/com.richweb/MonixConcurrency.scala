package com.richweb

// In order to evaluate tasks, we'll need a Scheduler
import monix.execution.Scheduler.Implicits.global
// Task is in monix.eval
import monix.eval.Task

import scala.concurrent.Await
import scala.concurrent.duration._

object MonixConcurrency {
  def timesTwo(n: Int): Task[Int] =
    Task {
      println(s"Running on ${Thread.currentThread.getName()}")
      Thread.sleep(2000)
      n * 2
    }

  def timesFour(n: Int): Task[Int] =
    for (a <- timesTwo(n); b <- timesTwo(n)) yield a + b

  /**
    timesTwo(n).flatMap { a =>
        timesTwo(n).map { b => a + b }
    */

  def timesFourParBAD(n: Int): Task[Int] = {
    // Will not trigger execution b/c Task is lazy
    val fa = timesTwo(n)
    val fb = timesTwo(n)
    // Evaluation will be sequential b/c of laziness
    for (a <- fa; b <- fb) yield a + b
  }

  def timesFourPar(n: Int): Task[Int] =
    Task.mapBoth(timesTwo(n), timesTwo(n))(_ + _)

  def sequence[A](list: List[Task[A]]): Task[List[A]] = {
    val seed = Task.now(List.empty[A])
    list
      .foldLeft(seed)((acc, f) => for (l <- acc; a <- f) yield a :: l)
      .map(_.reverse)
  }

  def main(args: Array[String]): Unit = {
    timesTwo(20).foreach { result =>
      println(s"Result: $result")
    }
    timesFour(20).foreach { result =>
      println(s"Result: $result")
    }

    println(timesFourPar(20).runSyncUnsafe())

    sequence(List(timesTwo(10), timesTwo(20), timesTwo(30))).foreach(println)
    Thread.sleep(500)
  }
}
