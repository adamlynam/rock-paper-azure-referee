package app

import services.{JsonApiBot, RoundRobin}

object Referee extends App {
  val botOneUrl: String = "http://localhost:9001"
  val botTwoUrl: String = "http://localhost:9002"

  val bots = List(new JsonApiBot(botOneUrl), new JsonApiBot(botTwoUrl))
  for ((bot,score) <- new RoundRobin().getResults(bots)) {
    println(bot.name + " won " + score + " game(s)")
  }

  sys.exit
}
