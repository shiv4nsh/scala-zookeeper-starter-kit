import sbt._
import Keys._

object Dependencies {

  val scala = "2.11.8"

  val resolvers = DefaultOptions.resolvers(snapshot = true) ++ Seq(
    "scalaz-releases" at "http://dl.bintray.com/scalaz/releases"
  )

  object playframework {
    val version      = play.core.PlayVersion.current

    val jdbc         = "com.typesafe.play"   %% "play-jdbc"                % version
    val cache        = "com.typesafe.play"   %% "play-cache"               % version
    val ws           = "com.typesafe.play"   %% "play-ws"                  % version
    val json         = "com.typesafe.play"   %% "play-json"                % version
    val specs2       = "com.typesafe.play"   %% "play-specs2"              % version            % "test"
  }


  val logback        = "ch.qos.logback"      %  "logback-classic"          % "1.1.3"
  val h2database     = "com.h2database"      %  "h2"                       % "1.4.187"          % "test"
  val scalatest      = "org.scalatest"       %% "scalatest"                % "2.2.4"            % "test"

  val playDependencies: Seq[ModuleID] = Seq(
    playframework.jdbc,
    playframework.cache,
    playframework.ws,
    playframework.json,
    playframework.specs2
  )
}
