package model

import org.scalatest.{FlatSpec, Matchers}

class GameLogicSpec extends FlatSpec with Matchers {
  "GameLogic winningMoveAgainst" should "calculate the winning move against a specific move" in {
    GameLogic.winningMoveAgainst(GameMove.ROCK) should be (GameMove.PAPER)
    GameLogic.winningMoveAgainst(GameMove.PAPER) should be (GameMove.SCISSORS)
    GameLogic.winningMoveAgainst(GameMove.SCISSORS) should be (GameMove.ROCK)
    GameLogic.winningMoveAgainst(GameMove.DYNAMITE) should be (GameMove.WATERBOMB)
    Set(GameMove.ROCK, GameMove.PAPER, GameMove.SCISSORS) contains GameLogic.winningMoveAgainst(GameMove.DYNAMITE)
  }

  "GameLogic losingMoveAgainst" should "calculate the winning move against a specific move" in {
    GameLogic.losingMoveAgainst(GameMove.ROCK) should be (GameMove.SCISSORS)
    GameLogic.losingMoveAgainst(GameMove.PAPER) should be (GameMove.ROCK)
    GameLogic.losingMoveAgainst(GameMove.SCISSORS) should be (GameMove.PAPER)
    Set(GameMove.ROCK, GameMove.PAPER, GameMove.SCISSORS) contains GameLogic.losingMoveAgainst(GameMove.WATERBOMB)
    GameLogic.losingMoveAgainst(GameMove.WATERBOMB) should be (GameMove.DYNAMITE)
  }

  "GameLogic calculateResult" should "return WIN, DRAW, LOSE depending on specific moves played" in {
    GameLogic.calculateResult(GameMove.ROCK, GameMove.ROCK) should be (GameResult.DRAW)
    GameLogic.calculateResult(GameMove.ROCK, GameMove.PAPER) should be (GameResult.LOSE)
    GameLogic.calculateResult(GameMove.ROCK, GameMove.SCISSORS) should be (GameResult.WIN)
    GameLogic.calculateResult(GameMove.ROCK, GameMove.DYNAMITE) should be (GameResult.LOSE)
    GameLogic.calculateResult(GameMove.ROCK, GameMove.WATERBOMB) should be (GameResult.WIN)
    GameLogic.calculateResult(GameMove.PAPER, GameMove.ROCK) should be (GameResult.WIN)
    GameLogic.calculateResult(GameMove.PAPER, GameMove.PAPER) should be (GameResult.DRAW)
    GameLogic.calculateResult(GameMove.PAPER, GameMove.SCISSORS) should be (GameResult.LOSE)
    GameLogic.calculateResult(GameMove.PAPER, GameMove.DYNAMITE) should be (GameResult.LOSE)
    GameLogic.calculateResult(GameMove.PAPER, GameMove.WATERBOMB) should be (GameResult.WIN)
    GameLogic.calculateResult(GameMove.SCISSORS, GameMove.ROCK) should be (GameResult.LOSE)
    GameLogic.calculateResult(GameMove.SCISSORS, GameMove.PAPER) should be (GameResult.WIN)
    GameLogic.calculateResult(GameMove.SCISSORS, GameMove.SCISSORS) should be (GameResult.DRAW)
    GameLogic.calculateResult(GameMove.SCISSORS, GameMove.DYNAMITE) should be (GameResult.LOSE)
    GameLogic.calculateResult(GameMove.SCISSORS, GameMove.WATERBOMB) should be (GameResult.WIN)
    GameLogic.calculateResult(GameMove.DYNAMITE, GameMove.ROCK) should be (GameResult.WIN)
    GameLogic.calculateResult(GameMove.DYNAMITE, GameMove.PAPER) should be (GameResult.WIN)
    GameLogic.calculateResult(GameMove.DYNAMITE, GameMove.SCISSORS) should be (GameResult.WIN)
    GameLogic.calculateResult(GameMove.DYNAMITE, GameMove.DYNAMITE) should be (GameResult.DRAW)
    GameLogic.calculateResult(GameMove.DYNAMITE, GameMove.WATERBOMB) should be (GameResult.LOSE)
    GameLogic.calculateResult(GameMove.WATERBOMB, GameMove.ROCK) should be (GameResult.LOSE)
    GameLogic.calculateResult(GameMove.WATERBOMB, GameMove.PAPER) should be (GameResult.LOSE)
    GameLogic.calculateResult(GameMove.WATERBOMB, GameMove.SCISSORS) should be (GameResult.LOSE)
    GameLogic.calculateResult(GameMove.WATERBOMB, GameMove.DYNAMITE) should be (GameResult.WIN)
    GameLogic.calculateResult(GameMove.WATERBOMB, GameMove.WATERBOMB) should be (GameResult.DRAW)
  }
}