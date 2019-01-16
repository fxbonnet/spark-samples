package sample

import java.util.concurrent.Executors

import org.scalatest.{FlatSpec, Matchers}

import scala.collection.parallel.{ForkJoinTaskSupport, ForkJoinTasks}
import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.concurrent.forkjoin.ForkJoinPool

class FutureTest extends FlatSpec with Matchers {
  val sequenceSize = 100
  val seq: Seq[String] = (1 to sequenceSize).map(i => s"item_$i")

  def slowFunction(input: String): String = {
    Thread.sleep(1000) // wait 1 second
    input
  }

  implicit val executor: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(sequenceSize))

  "Sequential execution" should "take 20 seconds" ignore {
    seq
      .map(slowFunction)
      .map(slowFunction)
      .foreach(println)
    succeed
  }

  "Parallel execution" should "take 2 seconds" in {
    val futureResult = Future.traverse(seq)(s => Future(slowFunction(slowFunction(s))))
    Await.result(futureResult, Duration.Inf).foreach(println)
    succeed
  }

  "Semi parallel execution" should "take 11 seconds" ignore {
    val futureResult1 = Future.traverse(seq)(s => Future(slowFunction(s)))
    val result1 = Await.result(futureResult1, Duration.Inf)
    result1
      .map(slowFunction)
      .foreach(println)
    succeed
  }

  "Using par (complex version)" should "take 2 seconds" in {
    val parallelSeq = seq.par
    parallelSeq.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(sequenceSize))
    val parallelSeq2 = parallelSeq
      .map(slowFunction)
    parallelSeq2.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(sequenceSize))
    parallelSeq2
      .map(slowFunction)
      .foreach(println)
    succeed
  }

  "Using par" should "take 20 seconds" ignore {
    ForkJoinTasks.defaultForkJoinPool
    seq
      .par.map(slowFunction)
      .par.map(slowFunction)
      .foreach(println)
    succeed
  }

}
