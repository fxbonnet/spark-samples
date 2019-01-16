package sample

import java.util.concurrent.Executors

import org.scalatest.tools.Durations
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import scala.language.postfixOps

class PromiseTest extends FlatSpec with Matchers {

  def waitForSomethingToHappen(callback: String => Unit): Unit = {
    implicit val executor: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
    Future {
      Thread.sleep(3000)
      if(true) throw new Exception
      callback("It happenned!")
    }
  }

  def event(): Future[String] = {
    val result = Promise[String]
    waitForSomethingToHappen(s => {
      if (s.length < 1000)
        result.success(s)
      else
        result.failure(new Exception("Too big"))
    })
    result.future
  }

  "Promise/Future exemple" should "return a result" ignore {
    val futureResult: Future[String] = event()
    val result: String = Await.result(futureResult, 5 seconds)
    result shouldEqual "It happenned!"
  }

  "Async execution" should "" in {
    implicit val executor: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
    val futureString = Future {
      Thread.sleep(1000)
      "OK"
    }
    futureString.foreach(println)
   println ( Await.result(futureString,5 seconds))
  }
}
