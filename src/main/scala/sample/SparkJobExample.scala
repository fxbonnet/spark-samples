package sample


object SparkJobExample extends SparkJob with App {
  println("Start of the job")
  val df = Seq(
    ("a", "b"),
    ("c", "d"),
    ("e", "f"))
    .toDF(Seq("col1", "col2"): _*)
  df.show
  println("End of the job")
}
