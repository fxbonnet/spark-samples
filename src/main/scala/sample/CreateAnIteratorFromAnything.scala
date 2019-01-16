package sample

object CreateAnIteratorFromAnything extends App {

  /**
    * Returns a limited number of Strings and then null. There is no method to check if there is a next entry or not.
    */
  class SomeNonStandardIterableThing {
    private var count = 0

    def next: String = {
      count = count + 1
      if (count > 5)
        null
      else
        s"Element # $count"
    }
  }

  val source = new SomeNonStandardIterableThing

  // Convert our object to a standard scala Iterator
  val iterator = Iterator.continually(source.next).span(_ != null)._1

  // Now we can use it in a for comprehension \o/
  for (str <- iterator) println(str)
}
