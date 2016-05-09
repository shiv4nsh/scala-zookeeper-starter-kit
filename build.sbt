import CommonSettings._
import Dependencies._
import play.routes.compiler.InjectedRoutesGenerator
import play.twirl.sbt.Import.TwirlKeys._

name := """scala-zookeeper-starter-kit"""

version := "1.0"

scalaVersion := scala

val scoverageSettings = Seq(
  coverageExcludedPackages := "<empty>;controllers.javascript;views.*;router",
  coverageExcludedFiles := "",
  coverageMinimum := 80,
  coverageFailOnMinimum := true
)

lazy val root = (
  project.in(file("."))
  aggregate(common, serviceA, serviceB)
)

lazy val common = (
  BaseProject("common")
  settings(libraryDependencies ++= playDependencies)
  settings(scoverageSettings: _*)
)

lazy val serviceA = (
  PlayProject("service-a")
  settings(libraryDependencies ++= playDependencies)
  settings(routesGenerator := InjectedRoutesGenerator)
  settings(scoverageSettings: _*)
) dependsOn(common)

lazy val serviceB = (
  PlayProject("service-b")
  settings(libraryDependencies ++= playDependencies)
  settings(routesGenerator := InjectedRoutesGenerator)
  settings(scoverageSettings: _*)
) dependsOn(common)
