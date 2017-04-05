name := """engine"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.10.5"

libraryDependencies += "com.github.scala-incubator.io" % "scala-io-file_2.10" % "0.4.3"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "3.2.0-SNAP4"

libraryDependencies ++= Seq(jdbc, anorm, cache, ws)

libraryDependencies += "org.projectlombok" % "lombok" % "1.16.14"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.3.2"

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"
