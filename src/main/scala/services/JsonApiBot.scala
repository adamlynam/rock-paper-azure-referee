package services

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import model.{Bot, GameMove}
import play.api.libs.json.Json
import play.api.libs.ws.ahc._
import play.api.libs.ws.JsonBodyWritables._

import scala.concurrent.Future

class JsonApiBot(botUrl: String) extends Bot {
  val startUrl: String = botUrl + "/start"
  val moveUrl: String = botUrl + "/move"

  var totalDynamite: Int = 0
  var usedDynamite: Int = 0

  import scala.concurrent.ExecutionContext.Implicits._

  // Create Akka system for thread and streaming management
  implicit val system = ActorSystem()
  system.registerOnTermination {
    System.exit(0)
  }
  implicit val materializer = ActorMaterializer()

  override def name = {
    botUrl
  }

  override def start(opponentName: String, pointsToWin: Int, totalTurns: Int, startingDynamite: Int) = {
    totalDynamite = startingDynamite
    val wsClient = StandaloneAhcWSClient()
    wsClient.url(startUrl)
      .withHttpHeaders("Content-Type" -> "application/json", "Accept" -> "application/json")
      .post(Json.obj(
        "opponentName" -> opponentName,
        "pointsToWin" -> pointsToWin,
        "maxRounds" -> totalTurns,
        "dynamiteCount" -> startingDynamite
      )).map(response => ())
      .andThen { case _ => wsClient.close() }
  }

  override def nextMove: Future[GameMove.Value] = {
    val wsClient = StandaloneAhcWSClient()
    wsClient.url(moveUrl)
      .withHttpHeaders("Content-Type" -> "application/json", "Accept" -> "application/json")
      .get.map(response => {
        val move = Json.fromJson[GameMove.Value](Json.parse(response.body)).get
        move match {
          case GameMove.DYNAMITE => {
            usedDynamite += 1
            if (totalDynamite - usedDynamite < 0) {
              GameMove.WATERBOMB
            } else {
              move
            }
          }
          case _ => move
        }
      })
      .andThen { case _ => wsClient.close() }
  }

  override def lastOpponentMove(lastMove: GameMove.Value) = {
    val wsClient = StandaloneAhcWSClient()
    wsClient.url(moveUrl)
      .withHttpHeaders("Content-Type" -> "application/json", "Accept" -> "application/json")
      .post(Json.obj(
        "opponentLastMove" -> lastMove
      )).map(response => ())
      .andThen { case _ => wsClient.close() }
  }
}
