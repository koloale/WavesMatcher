package com.wavesplatform.matcher.market

import java.util
import java.util.Comparator

import akka.actor.Props
import akka.persistence.{PersistentActor, RecoveryCompleted}

import com.wavesplatform.matcher.AssetPair
import com.wavesplatform.matcher.market.OrderBookActor._
import com.wavesplatform.matcher.model.{OrderItem, OrderType}
import com.wavesplatform.utils.ScorexLogging

object OrderBookActor {
  def props(assetPair: AssetPair): Props = Props(new OrderBookActor(assetPair))

  //protocol
  case class AddOrderCommand(order: OrderItem)
  case class AddOrderResponse(order: OrderItem)

  case object GetOrdersRequest
  case object GetBidOrdersRequest
  case object GetAskOrdersRequest
  case class GetOrdersResponse(items: Seq[OrderItem])

  // events
  sealed trait OrderEvent
  case class OrderAdded(order: OrderItem) extends OrderEvent
  case class OrderMatched(order: OrderItem) extends OrderEvent
}

case object BidComparator extends Comparator[Int] {
  def compare(o1: Int, o2: Int) = -o1.compareTo(o2)
}

case object AskComparator extends Comparator[Int] {
  def compare(o1: Int, o2: Int) = o1.compareTo(o2)
}

class OrderBookActor(assetPair: AssetPair) extends PersistentActor with ScorexLogging {
  override def persistenceId: String = assetPair.toString()

  private val asks = new OrderBook(assetPair, AskComparator)
  private val bids = new OrderBook(assetPair, BidComparator)

  override def receiveCommand: Receive = {
    case AddOrderCommand(order) =>
      handleAddOrder(order)
    case GetOrdersRequest =>
      sender() ! GetOrdersResponse(asks.flattenOrders ++ bids.flattenOrders)
    case GetAskOrdersRequest =>
      sender() ! GetOrdersResponse(asks.flattenOrders)
    case GetBidOrdersRequest =>
      sender() ! GetOrdersResponse(bids.flattenOrders)
  }

  override def receiveRecover: Receive = {
    case evt: OrderEvent => log.info("Event: {}", evt); applyEvent(evt)
    case RecoveryCompleted => log.info("Recovery completed!")
  }

  def handleAddOrder(order: OrderItem): Unit = {
    persistAsync(OrderAdded(order)) { evt =>
      place(order)
      sender() ! AddOrderResponse(order)
    }
  }

  private def applyEvent(orderEvent: OrderEvent) = orderEvent match {
    case OrderAdded(order) => place(order)
  }

  private def putOrder(order: OrderItem): Unit = {
    order.orderType match {
      case OrderType.BUY => bids.add(order)
      case OrderType.SELL => asks.add(order)
    }
  }

  private def place(order: OrderItem) {
    val (executedOrders, remaining) = order.orderType match {
      case OrderType.BUY => asks.execute(order)
      case OrderType.SELL => bids.execute(order)
    }

    if (executedOrders.nonEmpty) {
      log.info(s"${order.orderType} executed: {}", executedOrders)
    }

    if (remaining > 0) {
      putOrder(order.copy(amount = remaining))
    }
  }

}
