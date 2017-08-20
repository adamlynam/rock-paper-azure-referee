package services

import java.util.concurrent.TimeUnit

import model.{Bot, GameLogic, GameResult}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, FiniteDuration}

class RoundRobin {
  val POINTS_TO_WIN: Int = 1000
  val TOTAL_TURNS: Int = 2000
  val STARTING_DYNAMITE: Int = 100
  val BOT_RESPONSE_TIMEOUT_SECONDS = FiniteDuration(10, TimeUnit.SECONDS)

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
    // we actually need to await here to be sure each bot receives calls in the right order
    Await.ready(for {
      botOneResult <- botOne.start(opponentName = botTwo.name, pointsToWin = POINTS_TO_WIN, totalTurns = TOTAL_TURNS, startingDynamite = STARTING_DYNAMITE)
      botTwoResult <- botTwo.start(opponentName = botOne.name, pointsToWin = POINTS_TO_WIN, totalTurns = TOTAL_TURNS, startingDynamite = STARTING_DYNAMITE)
    } yield {}, BOT_RESPONSE_TIMEOUT_SECONDS)

    var botOneScore: Int = 0
    var botTwoScore: Int = 0

    for {
      i <- 1 to TOTAL_TURNS
    }{
      Await.ready(for {
          botOneMove <- botOne.nextMove
          botTwoMove <- botTwo.nextMove
      } yield {
        //println(botOneMove + " against " + botOneMove)
        // we actually need to await here to be sure each bot receives calls in the right order
        Await.ready(for {
          botOneReponse <- botOne.lastOpponentMove(botTwoMove)
          botOneReponse <- botTwo.lastOpponentMove(botOneMove)
        } yield {}, BOT_RESPONSE_TIMEOUT_SECONDS)

        GameLogic.calculateResult(botOneMove, botTwoMove) match {
          case GameResult.WIN => botOneScore += 1
          case GameResult.LOSE => botTwoScore += 1
          case GameResult.DRAW => Nil
        }
      }, BOT_RESPONSE_TIMEOUT_SECONDS)
    }

    println(botOne.name + " scored: " + botOneScore)
    println(botTwo.name + " scored: " + botTwoScore)
    if (botOneScore >= botTwoScore) botOne else botTwo
  }
}
