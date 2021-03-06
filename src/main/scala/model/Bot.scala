package model

import scala.concurrent.Future

trait Bot {
  def name: String
  def start(opponentName: String, pointsToWin: Int, totalTurns: Int, startingDynamite: Int): Future[Unit]
  def nextMove: Future[GameMove.Value]
  def lastOpponentMove(lastMove: GameMove.Value): Future[Unit]
}