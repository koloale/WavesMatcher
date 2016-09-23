package com.wavesplatform.matcher.market

import java.util.UUID

import com.wavesplatform.matcher._
import com.wavesplatform.matcher.market.MatchingEngine.{OrderCreated, OrderResponse}
import com.wavesplatform.matcher.model.OrderItem
import com.wavesplatform.utils.ScorexLogging
import play.api.libs.json.{JsValue, Json}

object MatchingEngine {
  def name = "market"

  sealed trait OrderResponse {
    val json: JsValue
  }
  case class OrderCreated(id: String) extends OrderResponse {
    implicit val formatter = Json.format[OrderCreated]
    val json = Json.toJson(this)
  }
  case object OrderCanceled extends OrderResponse {
    val json = Json.toJson("Order Canceled")
  }
}

class MatchingEngine extends ScorexLogging {
  var bids: Map[AssetPair, OrderBook] = Map()
  var asks: Map[AssetPair, OrderBook] = Map()

  private def buy(order: OrderItem) {
    val (executedOrders, remaining) = asks(order.assetPair).execute(order)

    if (executedOrders.nonEmpty) {
      log.info("Buy Executed: {}", executedOrders)
    }

    if (remaining > 0) {
      bids(order.assetPair).add(order.copy(amount = remaining))
    }
  }

  private def sell(order: OrderItem) {
    val (executedOrders, remaining) = bids(order.assetPair).execute(order)

    if (executedOrders.nonEmpty) {
      log.info("Sell Executed: {}", executedOrders)
    }

    if (remaining > 0) {
      asks(order.assetPair).add(order.copy(amount = remaining))
    }
  }

  def getBidOrders(assetPair: AssetPair): Seq[OrderItem]  = {
    bids(assetPair).flattenOrders
  }

  def getAskOrders(assetPair: AssetPair): Seq[OrderItem]  = {
    asks(assetPair).flattenOrders
  }

  def place(order: OrderItem): OrderResponse = {
    OrderCreated(UUID.randomUUID().toString)
  }

  /*def receive: Receive = {
    case order @ Order(clientId, OrderType.BUY, _, _, _) =>
      log.info("Market - received Buy message: {}", order)
      buy(order)
      sender() ! OrderCreated(clientId)
    case order @ Order(clientId, OrderType.SELL, _, _, _) =>
      log.info("Market - received Sell message: {}", order)
      sell(order)
      sender() ! OrderCreated(clientId)
  }*/
}
