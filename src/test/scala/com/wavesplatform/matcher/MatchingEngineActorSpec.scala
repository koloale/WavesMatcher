package com.wavesplatform.matcher

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class MatchingEngineActorSpec extends TestKit(ActorSystem("MatcherTest"))
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with ImplicitSender {

  /*"MatchingEngineActor" should {
    val orderSample = orderGenerator.sample
    "place an order to the matcher and preserve it after restart" in {
      val meActor = system.actorOf(Props(new MatchingEngineActor("asset-000001") with RestartableActor))

      orderSample match {
        case Some(order) =>
          meActor ! AddOrderCommand(order)
          expectMsg(AddOrderResponse(order))

          meActor ! RestartActor

          meActor ! GetOrdersRequest

          expectMsg(GetOrdersResponse(Seq(order)))
      }
    }
  }
  */
}
