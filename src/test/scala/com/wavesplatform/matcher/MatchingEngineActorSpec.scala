package com.wavesplatform.matcher

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}

import com.wavesplatform.matcher.fixtures.RestartableActor
import com.wavesplatform.matcher.fixtures.RestartableActor.RestartActor
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class MatchingEngineActorSpec extends TestKit(ActorSystem("MatcherTest"))
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with ImplicitSender {

  "MatchingEngineActor" should {
    "place an order to the smatcher and preserve it after restart" in {
      val shoppingCartActor = system.actorOf(Props(new MatchingEngineActor("asset-000001") with RestartableActor))

      /*shoppingCartActor ! AddItemCommand(shoppingItem)
      expectMsg(AddItemResponse(shoppingItem))

      shoppingCartActor ! RestartActor
      shoppingCartActor ! GetItemsRequest

      expectMsg(GetItemsResponse(Seq(shoppingItem)))*/
    }
  }
}
