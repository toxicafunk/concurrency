package com.richweb

class ThreadConcurrency(n: Int) extends Thread {
  var value: Option[Int] = None

  override def run(): Unit = {
    println(s"Running on ${Thread.currentThread.getName()}")
    Thread.sleep(2000)
    value = Some(n * 2)
  }
}

object ThreadConcurrency {

  def timesFour(n: Int): Option[Int] = {
    val t = new ThreadConcurrency(n)
    val t1 = new ThreadConcurrency(n)

    t.start()
    t1.start()

    t1.join()

    for {
      i <- t.value
      j <- t1.value
    } yield i + j
  }

  def main(args: Array[String]): Unit = {
    /*val t = new ThreadConcurrency(2)
    t.start()
    t.join()
    println(t.value)
    val t1 = new ThreadConcurrency(2)
    t1.start()
    while(t1.value.isEmpty) {
      println(s"Waiting on ${Thread.currentThread().getName()}...")
      Thread.sleep(500)
    }
    println(t1.value)*/

    println(timesFour(3))
  }
}
