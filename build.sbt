import CommonSettings._
import Dependencies._
import scoverage.ScoverageSbtPlugin.ScoverageKeys._

name := """scala-zookeeper-starterkit"""

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
    settings (libraryDependencies ++= Seq(zookeeper, curatorrecipies, curatorframework,curatorDiscovery,curatorTest))
    settings (scoverageSettings: _*)
  )

lazy val serviceA = (
  PlayProject("service-a")
    settings (libraryDependencies ++= playDependencies)
    settings (routesGenerator := InjectedRoutesGenerator)
    settings (scoverageSettings: _*)
  ) dependsOn (common)

lazy val serviceB = (
  PlayProject("service-b")
    settings (libraryDependencies ++= playDependencies)
    settings (routesGenerator := InjectedRoutesGenerator)
    settings (scoverageSettings: _*)
  ) dependsOn (common)

