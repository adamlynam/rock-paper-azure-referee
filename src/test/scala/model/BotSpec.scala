package model

import fakes.FakeBot
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

class BotSpec extends FlatSpec with Matchers {
  "Bot" should "return its name" in {
    val name = "[Fake Bot]"
    val bot: Bot = new FakeBot(botName = name, move = GameMove.ROCK)

    bot.name should be (name)
  }

  "Bot" should "accept a call to start" in {
    val bot: Bot = new FakeBot(botName = "[Fake Bot]", move = GameMove.ROCK)

    bot.start(opponentName = "[Opponent Name]", pointsToWin = 1000, totalTurns = 2000, startingDynamite = 100)
  }

  "Bot" should "return a valid move" in {
    val bot: Bot = new FakeBot(botName = "[Fake Bot]", move = GameMove.ROCK)

    for {
      move <- bot.nextMove
    } yield {
      move should be (GameMove.ROCK)
    }
  }

  "Bot" should "accept a call to lastOpponentMove" in {
    val bot: Bot = new FakeBot(botName = "[Fake Bot]", move = GameMove.ROCK)

    bot.lastOpponentMove(lastMove = GameMove.ROCK)
  }
}
