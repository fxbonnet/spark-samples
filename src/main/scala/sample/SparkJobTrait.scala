package sample

import org.apache.spark.sql.{SQLContext, SQLImplicits, SparkSession}

/**
  * Takes care of the boiler plate code:
  * - opens the spark session
  * - imports session.implicits
  */
trait SparkJob extends SQLImplicits with App {
  implicit val session: SparkSession = SparkSession.builder()
    .appName(getClass.getName)
    .master("local[*]")
    .getOrCreate()

  protected override def _sqlContext: SQLContext = session.sqlContext

  override def main(args: Array[String]): Unit = {
    println("Before the job")
    super.main(args)
    println("After the job")
  }
}

object MySparkJob extends SparkJob {
  println("Start of the job")
  val df = Seq(
    ("a", "b"),
    ("c", "d"),
    ("e", "f"))
    .toDF(Seq("col1", "col2"): _*)
  df.show
  println("End of the job")
}

