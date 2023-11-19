name := """Chigorin"""
organization := "org.torquemada"
version := "1.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

enablePlugins(DockerPlugin)

Universal / javaOptions += "-Dpidfile.path=/dev/null"

Docker / maintainer := "mjelnr@gmail.com"
Docker / daemonUser := "daemon"
Docker / packageName := "chigorin"
Docker / version := sys.env.getOrElse("BUILD_NUMBER", "1.1")
Docker / daemonUserUid  := None
Docker / dockerEnvVars += "play.http.secret.key"-> "57WDFg6UW/i:4LPdJ@Mly?Ltib]w>P<;4OOWpC2@x<T]`q]QYFtw5VbTx>Ky^[B9"
dockerBaseImage := "eclipse-temurin:17-jre-jammy"
dockerExposedPorts := Seq(9000)
dockerExposedVolumes := Seq("/opt/docker/logs",
                            "/opt/docker/public/engines",
                            "/var/lib/dpkg",
                            "/var/lib/apt/lists",
                            "/var/cache/apt/archives/partial")
dockerUpdateLatest := true
scalaVersion := "2.13.12"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
libraryDependencies += "org.projectlombok" % "lombok" % "1.18.30" % "provided"
