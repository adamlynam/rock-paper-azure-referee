package services

import model.{Bot, GameLogic, GameResult}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class RoundRobin {
  val POINTS_TO_WIN: Int = 1000
  val TOTAL_TURNS: Int = 2000
  val STARTING_DYNAMITE: Int = 100

  def getResults(bots: List[Bot]): Map[Bot, Int] = {
    var results: Map[Bot, Int] = Map[Bot, Int]().withDefaultValue(0)
    val uniqueGames = generateAllUniqueGames(bots)
    for (game <- uniqueGames) {
      val winningBot = runGame(botOne = game._1, botTwo = game._2)
      results = results.get(winningBot) match {
        case Some(winCount) => results + (winningBot -> (winCount + 1))
        case None => results + (winningBot -> 1)
      }
      results.updated(winningBot, results(winningBot) + 1)
    }
    results
  }

  private def generateAllUniqueGames(bots: List[Bot]): Seq[(Bot, Bot)] = {
    for {
      (x, idxX) <- bots.zipWithIndex
      (y, idxY) <- bots.zipWithIndex
      if idxX < idxY
    } yield (x, y)
  }

  private def runGame(botOne: Bot, botTwo: Bot) = {
    var botOneScore: Int = 0
    var botTwoScore: Int = 0

    for {
      i <- 1 to TOTAL_TURNS
    }{
      Await.ready(for {
          botOneMove <- botOne.nextMove
          botTwoMove <- botTwo.nextMove
      } yield {
        botOne.lastOpponentMove(botTwoMove)
        botTwo.lastOpponentMove(botOneMove)

//        botOneMove match {
//          case GameMove.DYNAMITE => {
//            botOneDynamiteRemaining -= 1
//            if (botOneDynamiteRemaining < 0) {
//              botOneMove = GameMove.WATERBOMB
//              println("BOT 1 NO DYNAMITE LEFT, PLAYING WATERBOMB")
//            }
//          }
//          case _ => botOneDynamiteRemaining
//        }
//        botTwoMove match {
//          case GameMove.DYNAMITE => {
//            botTwoDynamiteRemaining -= 1
//            if (botTwoDynamiteRemaining < 0) {
//              botTwoMove = GameMove.WATERBOMB
//              println("BOT 2 NO DYNAMITE LEFT, PLAYING WATERBOMB")
//            }
//          }
//          case _ => botTwoDynamiteRemaining
//        }

        GameLogic.calculateResult(botOneMove, botTwoMove) match {
          case GameResult.WIN => botOneScore += 1
          case GameResult.LOSE => botTwoScore += 1
          case GameResult.DRAW => Nil
        }
      }, Duration.Inf)
    }

    //println(botOne.name + " scored: " + botOneScore)
    //println(botTwo.name + " scored: " + botTwoScore)
    if (botOneScore >= botTwoScore) botOne else botTwo
  }
}
