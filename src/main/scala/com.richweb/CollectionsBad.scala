package com.richweb

import scala.collection._

object CollectionsBad extends App {
  import scala.concurrent.ExecutionContext.global
  def execute(body: => Unit) = global.execute(
    new Runnable { def run() = body }
  )

  val buffer = mutable.ArrayBuffer[Int]()
  def asyncAdd(numbers: Seq[Int]) = execute {
    buffer ++= numbers
    println(s"buffer = $buffer")
  }
  asyncAdd(0 until 10)
  asyncAdd(10 until 20)
  Thread.sleep(500)
}
