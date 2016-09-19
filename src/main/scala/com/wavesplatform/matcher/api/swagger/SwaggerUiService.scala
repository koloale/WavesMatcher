package com.wavesplatform.matcher.api.swagger

import com.wavesplatform.matcher.api.ApiRoute

case object SwaggerUiService extends ApiRoute {
  val route =
    path("swagger") { getFromResource("swagger-ui/index.html") } ~
      getFromResourceDirectory("swagger-ui")
}
