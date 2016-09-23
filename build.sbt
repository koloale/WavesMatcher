enablePlugins(JavaAppPackaging)

name := "WavesMatcher"
organization := "com.wavesplatform"
version := "1.0"
scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

lazy val swagger = Seq(
  "io.swagger" %% "swagger-scala-module" % "1.+",
  "io.swagger" % "swagger-core" % "1.+",
  "io.swagger" % "swagger-annotations" % "1.+",
  "io.swagger" % "swagger-models" % "1.+",
  "io.swagger" % "swagger-jaxrs" % "1.+",
  "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.+"
)

lazy val serializeLibs = Seq(
  "com.typesafe.play" %% "play-json" % "2.4.+"
)

lazy val loggingLibs = Seq(
  "ch.qos.logback" % "logback-classic" % "1.+",
  "ch.qos.logback" % "logback-core" % "1.+"
)

lazy val commonsLibs = Seq(
  "org.consensusresearch" %% "scrypto" % "1.0.4",
  "commons-net" % "commons-net" % "3.+"
)

libraryDependencies ++= {
  val akkaV       = "2.4.10"
  val scalaTestV  = "3.0.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-persistence" % akkaV,
    "com.github.dnvriend" %% "akka-persistence-inmemory" % "1.3.8" % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaV,
    "org.scalatest"     %% "scalatest" % scalaTestV % "test",
    "org.consensusresearch" % "scrypto_2.11" % "1.0.4"
  )
}

libraryDependencies ++= swagger ++
  serializeLibs ++
  commonsLibs ++
  loggingLibs

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.2" % "test"

Revolver.settings
