package com.wavesplatform

import com.typesafe.config.{Config, ConfigFactory}
import com.wavesplatform.matcher.api.HttpApi
import com.wavesplatform.system.{Core, CoreActors, Web}
import akka.util.Timeout

object Application extends App
  with Core
  with CoreActors
  with Web
  with RequestTimeout {

}

trait RequestTimeout {
  import scala.concurrent.duration._
  def requestTimeout(config: Config): Timeout = {
  val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}