package com.richweb

// On evaluation a Scheduler is needed
import monix.execution.Scheduler.Implicits.global
// For Task
import monix.eval._
// For Observable
import monix.reactive._
import monix.execution.Scheduler

object Parallelism {

  // A consumer that folds over the elements of the stream,
// producing a sum as a result
  val sumConsumer = Consumer.foldLeft[Long, Long](0L)(_ + _)

// For processing sums in parallel, useless of course, but can become
// really helpful for logic sprinkled with I/O bound stuff
  val loadBalancer = {
    Consumer
      .loadBalance(parallelism = 10, sumConsumer)
      .map(s => {
        println(s)
        s.sum
      })
  }

  val observable: Observable[Long] = Observable.range(0, 100000)//.dump("MORE")
// Our consumer turns our observable into a Task processing sums, w00t!
  val task: Task[Long] = observable.observeOn(Scheduler.io("IO")).consumeWith(loadBalancer)

  def main(args: Array[String]): Unit = {
// Consume the whole stream and get the result
    println(task.runSyncUnsafe())
  }

}
