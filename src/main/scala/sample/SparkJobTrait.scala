package sample

import org.apache.spark.sql.SparkSession


trait SparkJob extends App {
  implicit val session: SparkSession = SparkSession.builder()
    .appName(getClass.getName)
    .master("local[*]")
    .getOrCreate()

  override def main(args: Array[String]): Unit = {
    println("Before the job")
    super.main(args)
    println("After the job")
  }
}

object MySparkJob extends SparkJob {
  println("Start of the job")

  import session.implicits._

  val df = Seq(
    ("a", "b"),
    ("c", "d"),
    ("e", "f"))
    .toDF(Seq("col1", "col2"): _*)
  df.show
  println("End of the job")
}

