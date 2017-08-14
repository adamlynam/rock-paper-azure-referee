package app

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import model.{GameLogic, GameMove, GameResult}
import play.api.libs.ws._
import play.api.libs.ws.ahc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object Referee extends App {

  val botOneUrl = "http://localhost:9001"
  val botTwoUrl = "http://localhost:9002"

  val startPath = "/start"
  val movePath = "/move"

  import DefaultBodyReadables._
  import scala.concurrent.ExecutionContext.Implicits._

  // Create Akka system for thread and streaming management
  implicit val system = ActorSystem()
  system.registerOnTermination {
    System.exit(0)
  }
  implicit val materializer = ActorMaterializer()

  def postUrl(url: String) = {
    val wsClient = StandaloneAhcWSClient()
    wsClient.url(url).post("")
      .andThen { case _ => wsClient.close() }
  }

  def getMove(url: String): Future[GameMove.Value] = {
    val wsClient = StandaloneAhcWSClient()
    wsClient.url(url).get
      .map(response => GameMove.withName(response.body))
      .andThen { case _ => wsClient.close() }
  }

  def postOpponentMove(url: String, opponentMove: GameMove.Value) = {
    val wsClient = StandaloneAhcWSClient()
    wsClient.url(url).post(opponentMove.toString)
      .andThen { case _ => wsClient.close() }
  }

  val STARTING_DYNAMITE = 100
  var botOneDynamiteRemaining = STARTING_DYNAMITE
  var botTwoDynamiteRemaining = STARTING_DYNAMITE
  var botOneScore = 0
  var botTwoScore = 0

  postUrl(botOneUrl + startPath)
  postUrl(botTwoUrl + startPath)

  for (i <- 0 to 1000) {
    val botOneMoveFuture: Future[GameMove.Value] = getMove(botOneUrl + movePath)
    val botTwoMoveFuture: Future[GameMove.Value] = getMove(botTwoUrl + movePath)
    Await.ready(botOneMoveFuture, Duration.Inf)
    Await.ready(botTwoMoveFuture, Duration.Inf)
    var botOneMove = botOneMoveFuture.value.get.get
    var botTwoMove = botTwoMoveFuture.value.get.get
    postOpponentMove(botOneUrl + movePath, botTwoMove)
    postOpponentMove(botTwoUrl + movePath, botOneMove)
    botOneMove match {
      case GameMove.DYNAMITE => {
        botOneDynamiteRemaining -= 1
        if (botOneDynamiteRemaining < 0) {
          botOneMove = GameMove.WATERBOMB
          println("BOT 1 NO DYNAMITE LEFT, PLAYING WATERBOMB")
        }
      }
      case _ => botOneDynamiteRemaining
    }
    botTwoMove match {
      case GameMove.DYNAMITE => {
        botTwoDynamiteRemaining -= 1
        if (botTwoDynamiteRemaining < 0) {
          botTwoMove = GameMove.WATERBOMB
          println("BOT 2 NO DYNAMITE LEFT, PLAYING WATERBOMB")
        }
      }
      case _ => botTwoDynamiteRemaining
    }
    GameLogic.calculateResult(botOneMove, botTwoMove) match {
      case GameResult.WIN => {
        botOneScore += 1
        //println("BOT 1 WON: PLAYING " + botOneMove + " TO BEAT " + botTwoMove)
      }
      case GameResult.LOSE => {
        botTwoScore += 1
        //println("BOT 2 WON: PLAYING " + botTwoMove + " TO BEAT " + botOneMove)
      }
      case GameResult.DRAW => {
        //println("DRAW: BOTH PLAYED " + botOneMove)
      }
    }
  }

  println("Bot One Scored: " + botOneScore)
  println("Bot Two Scored: " + botTwoScore)
  system.terminate()
}
