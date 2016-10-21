package com.wavesplatform.matcher.api

import javax.ws.rs.Path

import com.wavesplatform.matcher.market.MatcherActor.OrderAccepted
import io.swagger.annotations._
import akka.http.scaladsl.Http
import akka.actor.{ActorRef, ActorRefFactory}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._

import scorex.api.http.ApiRoute
import scorex.settings.Settings

@Path("/orders")
@Api(value = "orders", produces = "application/json", consumes = "application/json")
case class OrdersService(ordersActor: ActorRef) extends ApiRoute {
  import akka.pattern.ask
  import scala.concurrent.duration._

  override val settings: Settings = ???
  override val context: ActorRefFactory = ???

  val route =
    buyRoute ~
    buy ~
    sell ~
    add1

  @Path("/buy")
  @ApiOperation(value = "Place a Buy Order", notes = "", nickname = "anonymousHello", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "pair", value = "String literal for the trading pair", required = true, dataType = "string", paramType = "query"),
    new ApiImplicitParam(name = "rate", value = "The rate at which to place the order", required = true, dataType = "double", paramType = "query"),
    new ApiImplicitParam(name = "amount", value = "The amount to buy", required = true, dataType = "double", paramType = "query")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Order Id", response = classOf[OrderAccepted]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def buy =
    pathPrefix("orders" / "buy") {
      pathEndOrSingleSlash {
        post {
          parameters("pair", "rate".as[Double], "amount".as[Double]) { (pair, rate, amount) =>
            complete((StatusCodes.Accepted, "Bid placed"))
          }
        }
      }
    }

  def buyRoute =
    pathPrefix("orders" / "buy1") {
      pathEndOrSingleSlash {
        post {
          complete((StatusCodes.Accepted, "Bid placed"))
        }
      }
    }

  @Path("/sell")
  @ApiOperation(value = "Place a Sell Order", notes = "", nickname = "anonymousHello", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "pair", value = "String literal for the trading pair", required = true, dataType = "string", paramType = "query"),
    new ApiImplicitParam(name = "rate", value = "The rate at which to place the order", required = true, dataType = "double", paramType = "query"),
    new ApiImplicitParam(name = "amount", value = "The amount to sell", required = true, dataType = "double", paramType = "query")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Order Id", response = classOf[OrderAccepted]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def sell =
    pathPrefix("orders" / "sell") {
      pathEndOrSingleSlash {
        post {
          parameters("pair", "rate".as[Double], "amount".as[Double]) { (pair, rate, amount) =>
            complete((StatusCodes.Accepted, "Sell placed"))
          }
        }
      }
    }

  @ApiOperation(value = "Add integers", notes = "", nickname = "addIntegers", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "\"numbers\" to sum", required = true,
      dataType = "String", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return sum", response = classOf[OrderAccepted]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def add1 =
    path("add1") {
      post {
        complete {"ddd"}
        }
      }

}
