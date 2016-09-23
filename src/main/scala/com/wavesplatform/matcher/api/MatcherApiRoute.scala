package com.wavesplatform.matcher.api

import javax.ws.rs.Path

import com.wavesplatform.matcher.api.http.WrongTransactionJson
import io.swagger.annotations._
import play.api.libs.json.{JsError, JsSuccess, Json}
import scala.util.{Failure, Success, Try}

import akka.actor.ActorRefFactory
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import com.wavesplatform.matcher.market.MatchingEngine
import com.wavesplatform.matcher.market.MatchingEngine._
import com.wavesplatform.matcher.model.OrderJson
import com.wavesplatform.matcher.system.PlayJsonSupport

@Path("/matcher")
@Api(value = "matcher", produces = "application/json", consumes = "application/json")
case class MatcherApiRoute() extends ApiRoute
  {

  override lazy val route =
    pathPrefix("matcher") {
      buy//~ cancel ~ getUnsigned
    }

  val matcher = new MatchingEngine
/*
  @Path("/order/cancel")
  @ApiOperation(value = "Cancel",
    notes = "Calncel your order",
    httpMethod = "POST",
    produces = "application/json",
    consumes = "application/json")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "body",
      value = "Json with data",
      required = true,
      paramType = "body",
      dataType = "com.wavesplatform.matcher.CancelJS",
      defaultValue = "{\n\t\"spendAddress\":\"spendAddress\",\n\t\"OrderID\":0,\n\t\"signature\":\"signature\"\n}"
    )
  ))
  def cancel: Route = path("order/cancel") {
    withCors {
      entity(as[String]) { body =>
        postJsonRoute {
          Try(Json.parse(body)).map { js =>
            js.validate[CancelJS] match {
              case err: JsError =>
                WrongTransactionJson(err).response
              case JsSuccess(cancelJS: CancelJS, _) =>
                cancelJS.cancel match {
                  case Success(cancelOrder) if cancelOrder.isValid =>
                    //TODO signed message what order is cancelled (with remaining amount)
                    JsonResponse(Json.obj("cancelled" -> matcher.cancel(cancelOrder)), StatusCodes.OK)
                  case _ => WrongJson.response
                }
            }
          }.getOrElse(WrongJson.response)
        }
      }
    }
  }
*/
  /*
  @Path("/order/place")
  @ApiOperation(value = "Place",
    notes = "Place new order",
    httpMethod = "POST",
    produces = "application/json",
    consumes = "application/json")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "body",
      value = "Json with data",
      required = true,
      paramType = "body",
      dataType = "com.wavesplatform.matcher.model.Order",
      defaultValue = "{\n\t\"spendAddress\":\"spendAddress\",\n\t\"spendTokenID\":\"spendTokenID\",\n\t\"receiveTokenID\":\"receiveTokenID\",\n\t\"price\":1,\n\t\"amount\":1,\n\t\"signature\":\"signature\"\n}"
    )
  ))
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Json with response or error")))
  def place: Route = path("order/place") {
    withCors {
      entity(as[String]) { body =>
        postJsonRoute {
          Try(Json.parse(body)).map { js =>
            js.validate[OrderJS] match {
              case err: JsError =>
                WrongTransactionJson(err).response
              case JsSuccess(orderjs: OrderJS, _) =>
                orderjs.order match {
                  case Success(order) if order.isValid =>
                    val resp = matcher.place(order)
                    JsonResponse(Json.obj("accepted" -> resp.json), StatusCodes.OK)
                  case _ => WrongJson.response
                }
            }
          }.getOrElse(WrongJson.response)
        }
      }
    }
  }*/

  @Path("/orders/buy")
  @ApiOperation(value = "Place",
    notes = "Place new order",
    httpMethod = "POST",
    produces = "application/json",
    consumes = "application/json")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "body",
      value = "Json with data",
      required = true,
      paramType = "body",
      dataType = "com.wavesplatform.matcher.model.OrderJson"
    )
  ))
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Json with response or error")))
  def buy =
    path("orders" / "buy") {
      pathEndOrSingleSlash {
        entity(as[String]) { body =>
          postJsonRoute {
            Try(Json.parse(body)).map { js =>
              js.validate[OrderJson] match {
                case err: JsError =>
                  WrongTransactionJson(err).response
                case JsSuccess(orderJson: OrderJson, _) =>
                  JsonResponse(Json.obj("accepted" -> "json"), StatusCodes.OK)
                  /*orderJson.order match {
                    case Success(order) if order.isValid =>
                      //val resp = matcher.place(order)
                      JsonResponse(Json.obj("accepted" -> resp.json), StatusCodes.OK)
                    case _ => WrongJson.response
                  }*/
              }
            }.getOrElse(WrongJson.response)
          }
        }
      }
    }

  /*def place: Route = path("order/place") {
  withCors {
    entity(as[Order]) { order =>
      postJsonRoute {
        if (order.isValid) {
          //val resp = matcher.place(order)
          JsonResponse(Json.obj("accepted" -> "123"), StatusCodes.OK)
        } else WrongJson.response
      }
    }
  }
}*/

/*
  @Path("/transaction/{address}")
  @ApiOperation(value = "Transactions", notes = "Get transactions to sign", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Json Waves node version")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "address", value = "Address", required = true, dataType = "String", paramType = "path")
  ))
  def getUnsigned: Route = {
    path("transaction" / Segment) { case address =>
      getJsonRoute {
        JsonResponse(Json.obj("version" -> "123"), StatusCodes.OK)
      }
    }
  }

  @Path("/transaction/sign")
  @ApiOperation(value = "Sign",
    notes = "Sign matched transaction",
    httpMethod = "POST",
    produces = "application/json",
    consumes = "application/json")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "body",
      value = "Json with data",
      required = true,
      paramType = "body",
      defaultValue = "{\n\t\"transactionId\":\"transactionId\",\n\t\"signature\":\"signature\"\n}"
    )
  ))
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Json with response or error")))
  def sign: Route = path("transaction/sign") {
    withCors {
      entity(as[String]) { body =>
        postJsonRoute {
          Try {
            val js = Json.parse(body)
            val signature = Base58.decode((js \ "signature").as[String]).get
            val transactionId = Base58.decode((js \ "transactionId").as[String]).get
            val signedTx = matcher.sign(transactionId, signature)
            signedTx match {
              case Success(tx) =>
                if (tx.isCompleted) {
                  val ntwMsg = Message(ExchangeTransactionMessageSpec, Right(tx), None)
                  application.networkController ! NetworkController.SendToNetwork(ntwMsg, Broadcast)
                }
                JsonResponse(Json.obj("signed" -> true, "broadcasted" -> tx.isCompleted), StatusCodes.OK)
              case Failure(e) =>
                JsonResponse(Json.obj("signed" -> false, "error" -> e.getMessage), StatusCodes.OK)
            }
          }.getOrElse(WrongJson.response)
        }
      }
    }
  }
*/

}


