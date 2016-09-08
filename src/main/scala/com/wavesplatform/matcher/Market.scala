package com.wavesplatform.matcher

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.wavesplatform.matcher.Market.OrderCreated
import com.wavesplatform.matcher.Orders._

object Market {
  def props(implicit timeout: Timeout) = Props(new Market)
  def name = "market"

  sealed trait OrderResponse
  case class OrderCreated(clientId: String) extends OrderResponse
  case object OrderCanceled extends OrderResponse
}

class Market(implicit timeout: Timeout) extends Actor{
  override def receive: Receive = {
    case Buy(clientId, instrument, price, quantity) => println(s"Buy ${quantity} ${instrument} for ${price}")
      sender() ! OrderCreated(clientId)
    case Sell(clientId, instrument, price, quantity) => println(s"Sell ${quantity} ${instrument} for ${price}")
      sender() ! OrderCreated(clientId)
  }
}
