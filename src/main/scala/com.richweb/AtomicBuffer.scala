package com.richweb

import java.util.concurrent.atomic._
import scala.collection._
import annotation.tailrec

class AtomicBuffer[T] {

  private val atomicBuffer = new AtomicReference[List[T]](Nil)

  @tailrec
  private def +=(x: T): Unit = {
    val xs = atomicBuffer.get
    val nxs = x :: xs
    if (!atomicBuffer.compareAndSet(xs, nxs)) this += x
  }

  def printLast() {
    println(atomicBuffer.get)
  }

  private val buffer = Seq.empty[T]
  def asyncAdd(numbers: Seq[Int]) = AtomicBuffer.execute {
    buffer.synchronized {
      val b = buffer ++ numbers
      println(s"buffer = $b")
    }
  }
}

object AtomicBuffer {

 import scala.concurrent.ExecutionContext.global
  def execute(body: => Unit) = global.execute(
    new Runnable { def run() = body }
  )

  def main(args: Array[String]): Unit = {
    val atomicBuffer = new AtomicBuffer[Int]
    atomicBuffer.asyncAdd(0 until 10)
    atomicBuffer.asyncAdd(10 until 20)

    (0 until 10).foreach(i => execute(atomicBuffer += i))
    Thread.sleep(500)

    atomicBuffer.printLast()
  }
}
