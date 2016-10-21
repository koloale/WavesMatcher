package com.wavesplatform.matcher.api

import com.wavesplatform.matcher.api.swagger.{CorsSupport, SwaggerDocService, SwaggerUiService}
import com.wavesplatform.system.{Core, CoreActors}

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, RouteConcatenation}

trait HttpApi extends RouteConcatenation
  with CorsSupport {
  this: CoreActors with Core =>


}
