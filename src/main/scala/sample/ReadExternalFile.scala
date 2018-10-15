package sample

import org.apache.spark.SparkFiles
import org.apache.spark.sql.SparkSession

object ReadExternalFile extends App {
  implicit val session: SparkSession = SparkSession.builder()
    .appName(getClass.getName)
    .master("local[*]")
    .getOrCreate()

  val sc = session.sparkContext

  // Can be either a local file, a file in HDFS (or other Hadoop-supported filesystems), or an HTTP, HTTPS or FTP URI.
  // Can also download recursively a directory.
  // Ex:
  // val path = "ftp://user:pwd/server/file"
  // Or:
  // val path = "s3://bucket/file"

  val path = "src/main/resources/test.zip"

  sc.addFile(path)
  var fileName = SparkFiles.get(path)

  println(s"Temporary file location: $fileName")

}
