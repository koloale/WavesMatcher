package com.wavesplatform.matcher

abstract class Order {

}

object Orders {
  case class Buy(clientId: String, instrument: String, price: Long, quantity: Long) extends Order
  case class Sell(clientId: String, instrument: String, price: Long, quantity: Long) extends Order
}