package com.richweb

object Synchronization {

  def thread(body: => Unit): Thread = {
    val t = new Thread {
      override def run() = body
    }
    t.start()
    t
  }

  /**
    * Always use `this` or equivalent so JVM knows which object monitor to synchronize, never assume scope
    * Also consider JVM may reorder statements as long as the semantic remains the same
    **/
  var uidCount = 0L
  def getUniqueId() = this.synchronized {
    val freshUid = uidCount + 1
    uidCount = freshUid
    freshUid
  }

  def printUniqueIds(n: Int): Unit = {
    val uids = for (i <- 0 until n) yield getUniqueId()
    println(s"Generated uids: $uids on ${Thread.currentThread.getName()}")
  }

  import scala.concurrent.ExecutionContext.global
  def execute(body: => Unit) = global.execute(
    new Runnable { def run() = body }
  )

  /**
    * Atomic operations are implemented in terms of a fundamental atomic operation,
    * which is compareAndSet. The compare-and-set operation, sometimes called
    * compare-and-swap (CAS), takes the expected previous value and the new value for
    * the atomic variable and atomically replaces the current value with the new value
    * only if the current value is equal to the expected value. compareAndSet is
    * implemented in terms of a processor instruction.
    */
  import java.util.concurrent.atomic._
  object AtomicUid {
    println("ATOMIC!")
    private val uid = new AtomicLong(0L)
    println(s"UID: $uid")
    def getUniqueId(): Long = uid.incrementAndGet()
    println(s"Got a unique id: ${getUniqueId()}")
  }

  def printAtomicUniqueIds(n: Int): Unit = {
    val uids = for (i <- 0 until n) yield AtomicUid.getUniqueId()
    println(s"Generated uids: $uids on ${Thread.currentThread.getName()}")
  }

  def main(args: Array[String]): Unit = {
    val t = thread(printUniqueIds(5))
    val t1 = thread(printUniqueIds(5))
    printUniqueIds(5)
    t.join()
    t1.join()

    execute(printAtomicUniqueIds(5))
    execute(printAtomicUniqueIds(5))

    printAtomicUniqueIds(5)
  }
}
