name := """oververse-universe"""
organization := "com.tol"

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies += guice
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.13.0-play26"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.13.0" % Test

coverageEnabled := true
coverageExcludedPackages := "<empty>;Reverse.*;.*MongoConnector;.*controllers.javascript; .*RoutesPrefix;.*Routes"
coverageMinimum := 100
coverageFailOnMinimum := true
routesGenerator := InjectedRoutesGenerator

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.tol.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.tol.binders._"
