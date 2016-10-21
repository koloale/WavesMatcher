package com.wavesplatform.system

import com.typesafe.config.ConfigFactory
import com.wavesplatform.matcher.api.{HttpApi, MatcherApiRoute}
import scala.concurrent.Future

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.RouteConcatenation
import akka.stream.ActorMaterializer

import com.wavesplatform.matcher.api.swagger.{CorsSupport, SwaggerDocService, SwaggerUiService}
import com.wavesplatform.matcher.market.MatcherActor
import com.wavesplatform.settings.CoreSettings

trait Core extends CoreSettings {

  implicit def system = ActorSystem("akka-matcher")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  sys.addShutdownHook(system.terminate())
}

trait CoreActors {
  this: Core =>

  val matcher = system.actorOf(MatcherActor.props(), MatcherActor.name)
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

trait Web extends RouteConcatenation
  with CorsSupport {
  this: CoreActors with Core =>

  val routes =
    MatcherApiRoute(matcher).route ~
      SwaggerUiService().route ~
      corsHandler(new SwaggerDocService(system, config).routes)

  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(routes, host, port)

}