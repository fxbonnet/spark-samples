package sample

import org.apache.spark.sql.{SQLContext, SQLImplicits, SparkSession}

/**
  * Takes care of the boiler plate code:
  * - opens the spark session
  * - imports session.implicits
  */
trait SparkJob extends SQLImplicits {
  implicit val session: SparkSession = SparkSession.builder()
    .appName(getClass.getName)
    .master("local[*]")
    .getOrCreate()

  protected override def _sqlContext: SQLContext = session.sqlContext
}
