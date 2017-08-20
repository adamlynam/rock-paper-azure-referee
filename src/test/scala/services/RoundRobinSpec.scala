package services

import fakes.FakeBot
import model.{Bot, GameMove}
import org.scalatest.{FlatSpec, Matchers}

class RoundRobinSpec extends FlatSpec with Matchers {


  "RoundRobin" should "accept a List[Bot] to run games for and return the correct winner" in {
    val rockBot = new FakeBot(botName = "Rock Bot", move = GameMove.ROCK)
    val paperBot = new FakeBot(botName = "Paper Bot", move = GameMove.PAPER)
    val scissorsBot = new FakeBot(botName = "Scissors Bot", move = GameMove.SCISSORS)
    val dynamiteBot = new FakeBot(botName = "Dynamite Bot", move = GameMove.DYNAMITE)
    val waterbombBot = new FakeBot(botName = "Waterbomb Bot", move = GameMove.WATERBOMB)
    val bots = List(rockBot, paperBot, scissorsBot, dynamiteBot, waterbombBot)
    val results = new RoundRobin().getResults(bots)

    printResults(results)
    results.maxBy(_._2)._1 should be (dynamiteBot)
  }

  def printResults(results: Map[Bot, Int]) = {
    for ((bot,score) <- results) {
      println(bot.name + " won " + score + " game(s)")
    }
  }
}

