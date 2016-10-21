enablePlugins(JavaAppPackaging)
enablePlugins(GatlingPlugin)

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
  "commons-net" % "commons-net" % "3.+",
  "org.skinny-framework" %% "skinny-validator" % "2.+"
)

lazy val gatlingLibs = Seq(
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.2" % "test",
  "io.gatling"            % "gatling-test-framework"    % "2.2.2" % "test"
)

val modulesVersion = "1.5.0-SNAPSHOT"
lazy val scorexLibs = Seq(
  "com.wavesplatform" %% "scorex-basics" % modulesVersion,
  "com.wavesplatform" %% "scorex-consensus" % modulesVersion,
  "com.wavesplatform" %% "scorex-transaction" % modulesVersion
)

val akkaV       = "2.4.11"
lazy val akkaLibs = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-stream" % akkaV,
  "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
  "com.typesafe.akka" %% "akka-persistence" % akkaV,
  "com.github.dnvriend" %% "akka-persistence-inmemory" % "1.3.8" % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % akkaV,
  "org.iq80.leveldb"            % "leveldb"          % "0.7",
  "org.fusesource.leveldbjni"   % "leveldbjni-all"   % "1.8",
  "com.github.romix.akka" %% "akka-kryo-serialization" % "0.4.1"
)

val scalaTestV  = "3.+"
val testLibs = Seq(
  "org.scalatest"     %% "scalatest" % scalaTestV % "test",
   "org.scalacheck" %% "scalacheck" % "1.+"
)


libraryDependencies ++=
  swagger ++
  serializeLibs ++
  commonsLibs ++
  loggingLibs ++
  gatlingLibs ++
  akkaLibs ++
  scorexLibs ++
  testLibs

Revolver.settings
