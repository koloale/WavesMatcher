package com.wavesplatform.matcher.api.swagger

import akka.actor.ActorRefFactory

import com.wavesplatform.settings.MatcherSettings
import scorex.api.http.ApiRoute

case class SwaggerUiService(implicit val settings: MatcherSettings, implicit val context: ActorRefFactory)
    extends ApiRoute {
  val route =
    path("swagger") { getFromResource("swagger-ui/index.html") } ~
      getFromResourceDirectory("swagger-ui")
}
