package sample

import scala.util.Try

object OptionWithBeautifulErrorMessage extends App {

  class MissingParameterException(name: String) extends Exception(s"Parameter $name is missing :(")

  val map = Map("key1" -> "value1")

  def missingParameter(name: String): Option[String] = throw new MissingParameterException(name)

  def getParameter(name: String): Option[String] = map.get(name).orElse(missingParameter(name))

  val result1 = getParameter("key1").get // should work
  println(result1)

  val result2 = getParameter("key2").get // should crash but with a nice message
  println(result2)

}
