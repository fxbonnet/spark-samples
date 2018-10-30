package sample

import org.scalatest.{FlatSpec, Matchers}
import sample.RockPaperScissors._

class RockPaperScissorsTest extends FlatSpec with Matchers {
  it should "work" in {
    winnerOf(Rock, Scissors) shouldBe Player1
    winnerOf(Scissors, Rock) shouldBe Player2
    winnerOf(Rock, Paper) shouldBe Player2
    winnerOf(Rock, Rock) shouldBe Draw
  }
}
