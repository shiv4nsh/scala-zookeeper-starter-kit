import sbt._

object Dependencies {

  val scala = "2.11.6"

  val resolvers = DefaultOptions.resolvers(snapshot = true) ++ Seq(
    "scalaz-releases" at "http://dl.bintray.com/scalaz/releases"
  )

  object playframework {
    val version = play.core.PlayVersion.current

    val jdbc = "com.typesafe.play" %% "play-jdbc" % version
    val cache = "com.typesafe.play" %% "play-cache" % version
    val ws = "com.typesafe.play" %% "play-ws" % version
    val json = "com.typesafe.play" %% "play-json" % version
    val specs2 = "com.typesafe.play" %% "play-specs2" % version % "test"
  }


  val scalatest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"

  val zookeeper = "org.apache.zookeeper" % "zookeeper" % "3.4.8"

  val curatorrecipies="org.apache.curator" % "curator-recipes" % "3.1.0"

  val curatorframework="org.apache.curator" % "curator-framework" % "3.1.0"

  val curatorDiscovery="org.apache.curator" % "curator-x-discovery" % "3.1.0"

  val curatorTest="org.apache.curator" % "curator-test" % "3.1.0"

  val playDependencies: Seq[ModuleID] = Seq(
    playframework.jdbc,
    playframework.cache,
    playframework.ws,
    playframework.json,
    playframework.specs2
  )

  val commonModuleDependencies: Seq[ModuleID] = playDependencies ++ Seq(zookeeper, curatorrecipies, curatorframework,curatorDiscovery,curatorTest)
}
