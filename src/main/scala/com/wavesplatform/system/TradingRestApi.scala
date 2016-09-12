package com.wavesplatform.system

import com.wavesplatform.matcher.{Instrument, Market, Order, OrderType}

import scala.concurrent.ExecutionContext

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.util.Timeout

class TradingRestApi(system: ActorSystem, timeout: Timeout)
    extends TradingRestRoutes {
    implicit val requestTimeout = timeout
    implicit def executionContext = system.dispatcher

  def createMarket = system.actorOf(Market.props, Market.name)
}

trait TradingRestRoutes extends MarketApi
  with OrderMarshalling {

  import StatusCodes._

  def routes: Route = buyRoute ~ sellRoute

  def buyRoute =
    pathPrefix("orders" / "buy") {
      pathEndOrSingleSlash {
        post {
          entity(as[String]) { buy =>
            onSuccess(placeOrder(Order("c1", OrderType.BUY, Instrument(buy), 0, 0))) {
              case Market.OrderCreated(clientId) => complete(Created, clientId)
            }
          }
        }
      }
    }

  def sellRoute =
    path("orders" / "sell") {
      pathEndOrSingleSlash {
        get {
            complete(Created, "123")
        }
      }
    }


}

trait MarketApi {
  import Market._

  def createMarket(): ActorRef

  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

  lazy val market = createMarket()

  def placeOrder(order: Order) =
    market.ask(order)
      .mapTo[OrderResponse]

}