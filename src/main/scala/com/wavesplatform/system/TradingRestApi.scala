package com.wavesplatform.system

import com.wavesplatform.matcher._

import scala.concurrent.ExecutionContext

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.util.Timeout

abstract class TradingRestApi(system: ActorSystem, timeout: Timeout)
    extends TradingRestRoutes {
    implicit val requestTimeout = timeout
    implicit def executionContext = system.dispatcher

  //def createMarket = system.actorOf(MatchingEngine.props, MatchingEngine.name)
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
            onSuccess(placeOrder(OrderItem("c1", OrderType.BUY, Instrument(buy), 0, 0))) {
              case MatchingEngine.OrderCreated(clientId) => complete(Created, clientId)
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
  import com.wavesplatform.matcher.MatchingEngine._

  def createMarket(): ActorRef

  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

  lazy val market = createMarket()

  def placeOrder(order: OrderItem) =
    market.ask(order)
      .mapTo[OrderResponse]

}