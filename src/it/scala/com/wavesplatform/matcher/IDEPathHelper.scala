package com.wavesplatform.matcher

import java.nio.file.Path

import io.gatling.commons.util.PathHelper._

object IDEPathHelper {

	val gatlingConfUrl: Path = getClass.getClassLoader.getResource("gatling.conf").toURI
	val projectRootDir = gatlingConfUrl.ancestor(4)

	val mavenSourcesDirectory = projectRootDir / "src" / "it" / "scala"
	val mavenResourcesDirectory = projectRootDir / "src" / "it" / "resources"
	val mavenTargetDirectory = projectRootDir / "target"
	val mavenBinariesDirectory = mavenTargetDirectory / "scala-2.11" / "test-classes"

	val dataDirectory = mavenResourcesDirectory / "data"
	val bodiesDirectory = mavenResourcesDirectory / "bodies"

	val recorderOutputDirectory = mavenSourcesDirectory
	val resultsDirectory = mavenTargetDirectory / "gatling"

	val recorderConfigFile = mavenResourcesDirectory / "recorder.conf"
}
