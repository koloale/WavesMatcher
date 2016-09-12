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

libraryDependencies ++= {
  val akkaV       = "2.4.10"
  val scalaTestV  = "2.2.6"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaV,
    "org.scalatest"     %% "scalatest" % scalaTestV % "test"
  )
}

libraryDependencies ++= swagger

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.2" % "test"

Revolver.settings
