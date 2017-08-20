package fakes

import model.{Bot, GameMove}

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

class FakeBot(botName: String, move: GameMove.Value) extends Bot {
  override def name = {
    botName
  }

  override def start(opponentName: String, pointsToWin: Int, totalTurns: Int, startingDynamite: Int): Unit = {

  }

  override def nextMove: Future[GameMove.Value] = {
    Future(move)
  }

  override def lastOpponentMove(lastMove: GameMove.Value): Unit = {

  }
}