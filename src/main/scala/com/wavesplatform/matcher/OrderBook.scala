package com.wavesplatform.matcher

import java.util
import java.util.Comparator

import scala.collection.JavaConversions._

class OrderBook(val instrument: Instrument, val comparator: Comparator[Double]) {
  private val priceOrders: util.TreeMap[Double, Orders] = new util.TreeMap[Double, Orders](comparator)

  def getBestOrders: Option[Orders] = {
    if (priceOrders.isEmpty) {
      None
    } else {
      Option(priceOrders.firstEntry.getValue)
    }
  }

  def add(order: Order) {
    priceOrders.putIfAbsent(order.price, new Orders(instrument, order.price))
    priceOrders.get(order.price) += order
  }

  def execute(order: Order): (Seq[Order], Double) = {
    getBestOrders match {
      case Some(bestOrders) =>
        if (comparator.compare(bestOrders.price, order.price) <= 0) {
          val res = bestOrders.execute(order)

          if (bestOrders.isEmpty) delete(bestOrders)
          res
        } else (Seq.empty, order.quantity)
      case None => (Seq.empty, order.quantity)
    }
  }

  def delete(orders: Orders) {
    priceOrders.remove(orders.price)
  }

  def flattenOrders: Seq[Order] = {
    priceOrders.values().flatMap(orders => orders.orders).toSeq
  }

}
