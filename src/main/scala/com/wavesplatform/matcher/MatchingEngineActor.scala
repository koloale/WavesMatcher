package com.wavesplatform.matcher

import akka.actor.Props
import akka.persistence.{PersistentActor, RecoveryCompleted}

import com.wavesplatform.matcher.MatchingEngineActor._
import com.wavesplatform.utils.ScorexLogging

object MatchingEngineActor {
  def props(assetId: String): Props = Props(new MatchingEngineActor(assetId))

  //protocol
  case class AddOrderCommand(order: OrderItem)
  case class AddOrderResponse(order: OrderItem)

  // events
  sealed trait OrderEvent
  case class OrderAdded(order: OrderItem) extends OrderEvent
}

class MatchingEngineActor(assetId: String) extends PersistentActor with ScorexLogging {
  private val matcher: MatchingEngine = new MatchingEngine()
  override def persistenceId: String = assetId
  private var state: Seq[OrderItem] = Seq.empty

  override def receiveCommand: Receive = {
    case AddOrderCommand(order) =>
      matcher.place(order)
      val apiSender = sender()
      persistAsync(OrderAdded(order)) { evt =>
        state = applyEvent(evt)
        apiSender ! AddOrderResponse(order)
      }
  }

  override def receiveRecover: Receive = {
    case evt: OrderEvent => state = applyEvent(evt)
    case RecoveryCompleted => log.info("Recovery completed!")
  }

  private def applyEvent(orderEvent: OrderEvent): Seq[OrderItem] = orderEvent match {
    case OrderAdded(order) => order +: state
  }
}
