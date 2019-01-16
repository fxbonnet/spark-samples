package sample

import java.util.concurrent.Executors

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutorService, Future}

/**
 * Inpired from http://trickbooter.com/post/2016-12-17-spark-parallel-execution/
 */
object ParallelAppTest extends App {
  val output = "target/testParallel"
  implicit val session: SparkSession = SparkSession.builder()
    .appName("ParallelJob")
    .master("local[*]")
    .getOrCreate()

  import session.implicits._

  val df1 = Seq(
    ("testA0", "testB0", "testC0"),
    ("testA0", "testB0", "testC0"))
    .toDF("colA", "colB", "colC")

  val df2 = Seq(
    ("testA0", "testB0", "testC0"),
    ("testA0", "testB0", "testC0"))
    .toDF("colA", "colB", "colC")

  val slowFunction: String => String = {
    str =>
      {
        Thread.sleep(3000)
        s"-$str"
      }
  }

  val slowUdf = udf(slowFunction)

  implicit val executionContext: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))

  println("Started")
  val result1 = Future {
    println("Started 1")
    df1
      .select(slowUdf(col("colA")) as "col1")
      .write.parquet("target/testParallel/df1")
    println("Finished 1")
  }
  val result2 = Future {
    println("Started 2")
    df2
      .select(slowUdf(col("colA")) as "col1")
      .write.parquet("target/testParallel/df2")
    println("Finished 2")
  }
  val result = Future.sequence(Seq(result1, result2))
  Await.result(result, Duration.Inf)

  println("Finished!")
}
