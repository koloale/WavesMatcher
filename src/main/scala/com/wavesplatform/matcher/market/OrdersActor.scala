package com.wavesplatform.matcher.market

import com.wavesplatform.matcher.MatchingEngine.OrderCreated
import com.wavesplatform.matcher.{OrderItem, OrderType}

import akka.actor.{Actor, ActorLogging}

class OrdersActor extends Actor
  with ActorLogging {

  override def receive: Receive = {
    case order @ OrderItem(clientId, OrderType.BUY, _, _, _) =>
      log.info("OrdersActor - received Buy message: {}", order)
      sender() ! OrderCreated(clientId)
    case order @ OrderItem(clientId, OrderType.SELL, _, _, _) =>
      log.info("OrdersActor - received Sell message: {}", order)
      sender() ! OrderCreated(clientId)
  }
}
