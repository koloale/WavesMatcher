package com.wavesplatform.matcher

import com.wavesplatform.StopSystemAfterAll
import org.scalatest._

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}

class MarketSpecification extends TestKit(ActorSystem("testSystem"))
  with PropSpecLike
  with Matchers
  with StopSystemAfterAll {

  /*property("Place Buy Orders") {
    val me = MatchingEngine()
    market ! Buy("client1", Instrument("EUR"), 405.12, 2)
    market ! Buy("client1", Instrument("EUR"), 409.12, 5)
    market ! Buy("client2", Instrument("EUR"), 405.12, 3)

    market.underlyingActor.getBidOrders(Instrument("EUR")) should equal (Seq(
      Buy("client1", Instrument("EUR"), 409.12, 5),
      Buy("client1", Instrument("EUR"), 405.12, 2),
      Buy("client2", Instrument("EUR"), 405.12, 3)
    ))
  }

  property("Place Sell Orders") {
    val market = TestActorRef[MatchingEngine]
    market ! Sell("client1", Instrument("EUR"), 100, 10)
    market ! Sell("client1", Instrument("EUR"), 100, 10)
    market ! Sell("client2", Instrument("EUR"), 110, 1)
    market ! Sell("client1", Instrument("EUR"), 99, 100)

    market.underlyingActor.getAskOrders(Instrument("EUR")) should equal (Seq(
      Sell("client1", Instrument("EUR"), 99, 100),
      Sell("client1", Instrument("EUR"), 100, 10),
      Sell("client1", Instrument("EUR"), 100, 10),
      Sell("client2", Instrument("EUR"), 110, 1)
    ))
  }

  property("Sell Market") {
    val market = TestActorRef[MatchingEngine]
    market ! Buy("client1", Instrument("EUR"), 100, 10)
    market ! Sell("client3", Instrument("EUR"), 100, 10)


    market.underlyingActor.getBidOrders(Instrument("EUR")) shouldBe empty
    market.underlyingActor.getAskOrders(Instrument("EUR")) shouldBe empty
  }

  property("Sell market with rest") {
    val market = TestActorRef[MatchingEngine]
    market ! Buy("client1", Instrument("EUR"), 100, 10)
    market ! Buy("client2", Instrument("EUR"), 100, 5)
    market ! Sell("client3", Instrument("EUR"), 100, 10)


    market.underlyingActor.getBidOrders(Instrument("EUR")) shouldEqual Seq(Buy("client2", Instrument("EUR"), 100, 5))
    market.underlyingActor.getAskOrders(Instrument("EUR")) shouldBe empty
  }

  property("Sell Not Enough Quantity") {
    val market = TestActorRef[MatchingEngine]
    market ! Buy("client1", Instrument("EUR"), 105, 10)
    market ! Sell("client3", Instrument("EUR"), 105, 15)

    market.underlyingActor.getBidOrders(Instrument("EUR")) shouldBe empty
    market.underlyingActor.getAskOrders(Instrument("EUR")) shouldEqual Seq(Sell("client3", Instrument("EUR"), 105, 5))
  }
*/
/*
  @Test def testSellNotEnoughQuantity() {
    market.placeOrder(new Order("C1", OrderType.BUY, Instrument.A, 105, 10))
    market.placeOrder(new Order("C3", OrderType.SELL, Instrument.A, 105, 15))
    assertEquals(asList(new Order("C1", OrderType.BUY, Instrument.A, 105, 10)), market.getBidOrders(Instrument.A))
    assertEquals(asList(new Order("C3", OrderType.SELL, Instrument.A, 105, 15)), market.getAskOrders(Instrument.A))
  }

  @Test def testBuyNotEnoughQuantity() {
    market.placeOrder(new Order("C1", OrderType.SELL, Instrument.A, 105, 10))
    market.placeOrder(new Order("C3", OrderType.BUY, Instrument.A, 105, 15))
    assertEquals(asList(new Order("C3", OrderType.BUY, Instrument.A, 105, 15)), market.getBidOrders(Instrument.A))
    assertEquals(asList(new Order("C1", OrderType.SELL, Instrument.A, 105, 10)), market.getAskOrders(Instrument.A))
  }

  @Test def testBuyDifferentInstruments() {
    market.placeOrder(new Order("C1", OrderType.SELL, Instrument.A, 105, 10))
    market.placeOrder(new Order("C3", OrderType.BUY, Instrument.B, 105, 10))
    assertEquals(asList(new Order("C3", OrderType.BUY, Instrument.B, 105, 10)), market.getBidOrders(Instrument.B))
    assertEquals(asList, market.getBidOrders(Instrument.A))
    assertEquals(asList(new Order("C1", OrderType.SELL, Instrument.A, 105, 10)), market.getAskOrders(Instrument.A))
    assertEquals(asList, market.getAskOrders(Instrument.B))
  }*/
}
