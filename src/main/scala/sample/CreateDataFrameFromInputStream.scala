package sample

import com.univocity.parsers.csv._
import org.apache.commons.io.IOUtils
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.encoders._
import org.apache.spark.sql.types._
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

/**
  * Shows how to create a DataFrame from an inputStream. Unfortunately Spark can create a DataFrame from a Stream but
  * requires to fully load the Stream while building the DataFrame so this can only be used for something small enough
  * to fit in memory.
  */

object CreateDataFrameFromInputStream extends App {
  val log = LoggerFactory.getLogger(getClass)

  val inputStream = IOUtils.toInputStream(
    """
      |col_1,col_2
      |a,b
      |c,d
      |e,f
    """.stripMargin)

  implicit val session: SparkSession = SparkSession.builder()
    .appName(getClass.getName)
    .master("local[*]")
    .getOrCreate()

  import session.implicits._

  val settings = new CsvParserSettings()
  settings.getFormat.setLineSeparator("\n")
  val parser = new CsvParser(settings)

  val csvRows = parser.iterate(inputStream).iterator().asScala.map(_.toSeq)
  csvRows.hasNext // to start the parser

  val columns = csvRows.next()
  val encoder = RowEncoder(StructType(columns.map(StructField(_, StringType, nullable = true))))

  val arraysDf = csvRows.toStream.toDF
  // Creation of org.apache.spark.sql.catalyst.plans.logical.LocalRelation is implicitely converting the stream to
  // a seq which triggers loading of all the elements of the stream to memory :(
  // Stacktrace:
  //    <init>:50, LocalRelation (org.apache.spark.sql.catalyst.plans.logical)
  //    createDataset:472, SparkSession (org.apache.spark.sql)
  //    createDataset:377, SQLContext (org.apache.spark.sql)
  //    localSeqToDatasetHolder:228, SQLImplicits (org.apache.spark.sql)
  //    delayedEndpoint$sample$CreateDataFrameFromInputStream$1:40, CreateDataFrameFromInputStream$ (sample)
  //    apply:12, CreateDataFrameFromInputStream$delayedInit$body (sample)
  //    apply$mcV$sp:34, Function0$class (scala)
  //    apply$mcV$sp:12, AbstractFunction0 (scala.runtime)
  //    apply:76, App$$anonfun$main$1 (scala)
  //    apply:76, App$$anonfun$main$1 (scala)
  //    foreach:392, List (scala.collection.immutable)
  //    foreach:35, TraversableForwarder$class (scala.collection.generic)
  //    main:76, App$class (scala)
  //    main:12, CreateDataFrameFromInputStream$ (sample)
  //    main:-1, CreateDataFrameFromInputStream (sample)

  val df = arraysDf.map(s => Row.fromSeq(s.getAs[Seq[String]](0)))(encoder)

  df.show()
  df.show()
  log.debug(s"Count: ${df.count()}")
  //  df.foreach(r => println(r.get(1)))
}

