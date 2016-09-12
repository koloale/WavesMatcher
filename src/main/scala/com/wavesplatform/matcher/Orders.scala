package com.wavesplatform.matcher

import scala.collection.mutable.ArrayBuffer

class Orders(val instrument: Instrument, val price: Double) {
  var orders = Vector.empty[Order]

  def += (order: Order) {
    require(order.price == price && order.instrument == instrument)
    orders = orders :+ order
  }

  def execute(order: Order): (Seq[Order], Double) = {
    var remainingQuantity = order.quantity

    var (executed, rest) = orders.span { placedOrder =>
      if (placedOrder.quantity <= remainingQuantity) {
        remainingQuantity -= placedOrder.quantity
        true
      } else {
        false
      }
    }

    rest match {
      case partOrder +: others if remainingQuantity > 0 =>
        executed = executed :+ partOrder.copy(quantity = partOrder.quantity - remainingQuantity)
        rest = others
        remainingQuantity = 0
      case _ =>
    }

    orders = rest
    (executed, remainingQuantity)
  }

  def isEmpty: Boolean = orders.isEmpty
}
