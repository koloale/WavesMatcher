package com.wavesplatform.system

import com.typesafe.config.ConfigFactory
import com.wavesplatform.matcher.api.HttpApi

import scala.concurrent.Future

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer

trait Core {

  implicit def system = ActorSystem("akka-matcher")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher
  implicit val config = ConfigFactory.load()

  sys.addShutdownHook(system.terminate())
}

trait CoreActors {
  this: Core =>

}

/*trait Web {
  this: HttpApi with CoreActors with Core =>

  Http().bindAndHandle(routes, host, port)

  val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(routes, host, port)

  bindingFuture.map { serverBinding =>
    //log.info(s"RestApi bound to ${serverBinding.localAddress} ")
  }.onFailure {
    case ex: Exception =>
      //log.error(ex, "Failed to bind to {}:{}!", host, port)
      system.terminate()
  }
}*/

trait Web {
  this: HttpApi with CoreActors with Core =>

  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(routes, host, port)

}