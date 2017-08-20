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

  system.terminate()
}
