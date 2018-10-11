package sample

import org.apache.spark.sql.catalyst.encoders.{ExpressionEncoder, RowEncoder}
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

object CountRowsWithoutPerformanceCost extends App {
  implicit val session: SparkSession = SparkSession.builder()
    .appName(getClass.getName)
    .master("local[*]")
    .getOrCreate()

  import session.implicits._

  val df = Seq(
    ("a", "b"),
    ("c", "d"),
    ("e", "f"))
    .toDF(Seq("col1", "col2"): _*)

  implicit val encoder: ExpressionEncoder[Row] = RowEncoder(df.schema)
  val rowCount = session.sparkContext.longAccumulator
  val cdf = df.map { row =>
    rowCount.add(1)
    row
  }
  cdf.write.mode(SaveMode.Overwrite).csv("target/test.csv")

  println(s"Row count: ${rowCount.sum}")
}
