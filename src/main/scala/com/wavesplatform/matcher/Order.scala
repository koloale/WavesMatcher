package com.wavesplatform.matcher

import com.wavesplatform.matcher.OrderType.OrderType

/*sealed trait Order {
  val orderType:OrderType
  val instrument: Instrument
  val price: Double
  val quantity: Double
}*/

case class Order(id: String, orderType: OrderType, instrument: Instrument, price: Double, quantity: Double) {

}

object Buy {
  def apply(clientId: String, instrument: Instrument, price: Double, quantity: Double) =
    Order(clientId, OrderType.BUY,  instrument, price, quantity)
}


object Sell {
  def apply(clientId: String, instrument: Instrument, price: Double, quantity: Double) =
    Order(clientId, OrderType.SELL,  instrument, price, quantity)
}