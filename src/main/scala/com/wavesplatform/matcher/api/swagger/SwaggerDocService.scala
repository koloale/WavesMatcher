package com.wavesplatform.matcher.api.swagger

import com.github.swagger.akka.model.Info
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}
import com.typesafe.config.Config
import com.wavesplatform.matcher.api.OrdersService
import io.swagger.models.Swagger

import scala.reflect.runtime.universe._

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer

class SwaggerDocService(system: ActorSystem, config: Config)
  extends SwaggerHttpService with HasActorSystem {

  override implicit val actorSystem: ActorSystem = system
  override implicit val materializer: ActorMaterializer = ActorMaterializer()

  override val host = "localhost:" + config.getString("http.port")
  override val apiDocsPath: String = "api-docs"

  override val apiTypes: Seq[Type] = Seq(
    typeOf[OrdersService])

  override val info: Info = Info("The Web Interface to the WavesMatcher Rest API",
    "1.0.0",
    "WavesMatcher API"
  )

  //Let swagger-ui determine the host and port
  override def swaggerConfig = new Swagger().basePath(prependSlashIfNecessary(basePath)).info(info).scheme(scheme)
}