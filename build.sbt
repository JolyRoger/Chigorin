name := """Chigorin"""
organization := "org.torquemada"
version := "1.1-SNAPSHOT"

import com.typesafe.sbt.web.SbtWeb.autoImport._

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.12"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
libraryDependencies += "org.projectlombok" % "lombok" % "1.18.30" % "provided"
Assets / LessKeys.less / includeFilter := "*.less"
enablePlugins(SbtWeb)
