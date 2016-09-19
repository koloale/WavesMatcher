package com.wavesplatform.matcher

import java.util.{Comparator, UUID}

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.Timeout

import com.wavesplatform.matcher.MatchingEngine.{OrderCreated, OrderResponse}
import com.wavesplatform.utils.ScorexLogging
import play.api.libs.json.{JsObject, JsValue, Json}

case object BidComparator extends Comparator[Double] {
  def compare(o1: Double, o2: Double) = -o1.compareTo(o2)
}

case object AskComparator extends Comparator[Double] {
  def compare(o1: Double, o2: Double) = o1.compareTo(o2)
}

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
  var bids: Map[Instrument, OrderBook] = Instruments.values.map(i => i -> new OrderBook(i, BidComparator)).toMap
  var asks: Map[Instrument, OrderBook] = Instruments.values.map(i => i -> new OrderBook(i, AskComparator)).toMap

  private def buy(order: OrderItem) {
    val (executedOrders, remaining) = asks(order.instrument).execute(order)

    if (executedOrders.nonEmpty) {
      log.info("Buy Executed: {}", executedOrders)
    }

    if (remaining > 0) {
      bids(order.instrument).add(order.copy(quantity = remaining))
    }
  }

  private def sell(order: OrderItem) {
    val (executedOrders, remaining) = bids(order.instrument).execute(order)

    if (executedOrders.nonEmpty) {
      log.info("Sell Executed: {}", executedOrders)
    }

    if (remaining > 0) {
      asks(order.instrument).add(order.copy(quantity = remaining))
    }
  }

  def getBidOrders(instrument: Instrument): Seq[OrderItem]  = {
    bids(instrument).flattenOrders
  }

  def getAskOrders(instrument: Instrument): Seq[OrderItem]  = {
    asks(instrument).flattenOrders
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
