package sample

import java.util.zip.ZipInputStream

import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.IOUtils
import org.apache.spark.input.PortableDataStream
import org.apache.spark.sql.SparkSession

/**
  * Unzip a file and write each file in the file system.
  */
object ReadZipFile extends App {
  implicit val session: SparkSession = SparkSession.builder()
    .appName(getClass.getName)
    .master("local[*]")
    .getOrCreate()

  val sc = session.sparkContext

  sc.binaryFiles("src/main/resources/test.zip").foreach { case (fileName, is) =>
    unzip(fileName, is).foreach(csvFile =>
      session.read.option("header", "true").csv(csvFile).show
    )
  }

  def unzip(fileName: String, is: PortableDataStream): Seq[String] = {
    val fs = FileSystem.newInstance(sc.hadoopConfiguration)
    val zis = new ZipInputStream(is.open())
    try {
      val iterator = Iterator.continually(zis.getNextEntry).takeWhile(_ != null)
      val files = iterator.map(entry => {
        // Could be s3://...
        val filePath = "target/files/" + entry.getName
        val out = fs.create(new Path(filePath))
        try {
          IOUtils.copyBytes(zis, out, 1024 * 1024)
        } finally out.close()
        filePath
      }).toSeq
      val str = files.mkString(",") // Note: mkString triggers the evaluation. Necessary before closing the inputstream
      println(s"Saved files: $str")
      files
    } finally
      zis.close()
  }
}
