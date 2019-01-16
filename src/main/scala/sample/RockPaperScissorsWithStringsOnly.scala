package sample

object RockPaperScissorsWithStringsOnly {
  implicit class Item(item1: String) {
    def beats(item2: String): (String, String) = (item1, item2)
  }

  val rules = List(
    "scissors" beats "paper",
    "paper" beats "rock",
    "rock" beats "scissors")

  def winnerOf(item1: String, item2: String): String =
    if (rules contains (item1 beats item2)) "player1"
    else if (rules contains (item2 beats item1)) "player2"
    else "draw"
}