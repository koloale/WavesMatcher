package com.wavesplatform.matcher

import java.util.Comparator

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.Timeout
import com.wavesplatform.matcher.Market.OrderCreated

case object BidComparator extends Comparator[Double] {
  def compare(o1: Double, o2: Double) = -o1.compareTo(o2)
}

case object AskComparator extends Comparator[Double] {
  def compare(o1: Double, o2: Double) = o1.compareTo(o2)
}

object Market {
  def props(implicit timeout: Timeout) = Props(new Market)
  def name = "market"

  sealed trait OrderResponse
  case class OrderCreated(clientId: String) extends OrderResponse
  case object OrderCanceled extends OrderResponse
}

class Market extends Actor
  with ActorLogging {
  var bids: Map[Instrument, OrderBook] = Instruments.values.map(i => i -> new OrderBook(i, BidComparator)).toMap
  var asks: Map[Instrument, OrderBook] = Instruments.values.map(i => i -> new OrderBook(i, AskComparator)).toMap

  private def buy(order: Order) {
    val (executedOrders, remaining) = asks(order.instrument).execute(order)

    if (executedOrders.nonEmpty) {
      log.info("Buy Executed: {}", executedOrders)
    }

    if (remaining > 0) {
      bids(order.instrument).add(order.copy(quantity = remaining))
    }
  }

  private def sell(order: Order) {
    val (executedOrders, remaining) = bids(order.instrument).execute(order)

    if (executedOrders.nonEmpty) {
      log.info("Sell Executed: {}", executedOrders)
    }

    if (remaining > 0) {
      asks(order.instrument).add(order.copy(quantity = remaining))
    }
  }

  def getBidOrders(instrument: Instrument): Seq[Order]  = {
    bids(instrument).flattenOrders
  }

  def getAskOrders(instrument: Instrument): Seq[Order]  = {
    asks(instrument).flattenOrders
  }

  override def receive: Receive = {
    case order @ Order(clientId, OrderType.BUY, _, _, _) =>
      log.info("Market - received Buy message: {}", order)
      buy(order)
      sender() ! OrderCreated(clientId)
    case order @ Order(clientId, OrderType.SELL, _, _, _) =>
      log.info("Market - received Sell message: {}", order)
      sell(order)
      sender() ! OrderCreated(clientId)
  }
}
