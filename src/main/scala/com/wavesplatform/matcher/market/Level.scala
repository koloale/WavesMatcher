package com.wavesplatform.matcher.market

import com.wavesplatform.matcher.{AssetId, AssetPair}
import com.wavesplatform.matcher.model.OrderItem

class Level(val assetPair: AssetPair, val price: Int) {
  var orders = Vector.empty[OrderItem]

  def += (order: OrderItem) {
    require(order.price == price && order.assetPair == assetPair)
    orders = orders :+ order
  }

  def execute(order: OrderItem): (Seq[OrderItem], Long) = {
    var remainingAmount = order.amount

    var (executed, rest) = orders.span { placedOrder =>
      if (placedOrder.amount <= remainingAmount) {
        remainingAmount -= placedOrder.amount
        true
      } else {
        false
      }
    }

    rest match {
      case partOrder +: others if remainingAmount > 0 =>
        executed = executed :+ partOrder.copy(amount = remainingAmount)
        rest = partOrder.copy(amount = partOrder.amount - remainingAmount) +: others
        remainingAmount = 0
      case _ =>
    }

    orders = rest
    (executed, remainingAmount)
  }

  def isEmpty: Boolean = orders.isEmpty
}
