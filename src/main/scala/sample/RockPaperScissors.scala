package sample

object RockPaperScissors {

  sealed trait Item {def beats(other: Item): Rule = Rule(this, other)}
  case object  Rock extends Item
  case object  Paper extends Item
  case object  Scissors extends Item

  sealed trait Result
  case object Player1 extends Result
  case object Player2 extends Result
  case object Draw extends Result

  case class Rule(item1: Item, item2: Item) // item1 beats item2

  val rules = List(
    Scissors beats Paper,
    Paper beats Rock,
    Rock beats Scissors)

  def winnerOf(item1: Item, item2: Item): Result =
    if (rules contains(item1 beats item2))
      Player1
    else if (rules contains(item2 beats item1))
      Player2
    else
      Draw
}