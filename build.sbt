name := "rock-paper-azure-referee"

version := "0.1"

scalaVersion := "2.12.3"

libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.4"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"