name := """play-vuejs"""
organization := "com.kouares"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test

// ScalikeJDBC
libraryDependencies ++= Seq(
  jdbc,
  "mysql"           % "mysql-connector-java"          % "5.1.24",
  "org.scalikejdbc" %% "scalikejdbc"                  % "3.0.1",
  "org.scalikejdbc" %% "scalikejdbc-config"           % "3.0.1",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.6.0"
)

// ScalikeJDBC Model Genarate
scalikejdbcSettings

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.kouares.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.kouares.binders._"
