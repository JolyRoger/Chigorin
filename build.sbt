name := """engine"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.10.5"

libraryDependencies += "org.yaml" % "snakeyaml" % "1.15"

libraryDependencies += "com.github.scala-incubator.io" % "scala-io-file_2.10" % "0.4.3"

libraryDependencies ++= Seq(jdbc, anorm, cache, ws)

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

