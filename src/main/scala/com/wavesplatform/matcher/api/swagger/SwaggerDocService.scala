package com.wavesplatform.matcher.api.swagger

import java.lang.annotation.Annotation
import java.util

import com.github.swagger.akka.model.Info
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}
import com.typesafe.config.Config
import com.wavesplatform.matcher.api.{MatcherApiRoute, OrdersService}
import io.swagger.models.{Model, Swagger}
import scala.reflect.runtime.universe._

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer

import io.swagger.converter.{ModelConverter, ModelConverterContext, ModelConverters}
import io.swagger.models.properties.Property

object ByteArrayConverter extends ModelConverter {
  override def resolveProperty(aType: java.lang.reflect.Type, context: ModelConverterContext, annotations:
  Array[Annotation], chain:
  util.Iterator[ModelConverter]): Property = {
    if (chain.hasNext())
      chain.next().resolveProperty(aType, context, annotations, chain)
    else
      null
  }

  override def resolve(aType: java.lang.reflect.Type, context: ModelConverterContext, chain: util.Iterator[ModelConverter]): Model =
    chain.next().resolve(aType, context, chain)
}

class SwaggerDocService(system: ActorSystem, config: Config)
  extends SwaggerHttpService with HasActorSystem {

  override implicit val actorSystem: ActorSystem = system
  override implicit val materializer: ActorMaterializer = ActorMaterializer()

  override val host = "localhost:" + config.getString("http.port")
  override val apiDocsPath: String = "api-docs"

  override val apiTypes: Seq[Type] = Seq(
    scala.reflect.runtime.universe.typeOf[MatcherApiRoute])

  override val info: Info = Info("The Web Interface to the WavesMatcher Rest API",
    "1.0.0",
    "WavesMatcher API"
  )

  ModelConverters.getInstance().addConverter(ByteArrayConverter)
  //Let swagger-ui determine the host and port
  override def swaggerConfig = new Swagger().basePath(prependSlashIfNecessary(basePath)).info(info).scheme(scheme)
}