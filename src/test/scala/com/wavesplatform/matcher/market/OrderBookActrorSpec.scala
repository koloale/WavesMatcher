package com.wavesplatform.matcher.market

import akka.actor.{ActorSystem, Props}
import akka.persistence.inmemory.extension.{InMemoryJournalStorage, InMemorySnapshotStorage, StorageExtension}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}

import com.wavesplatform.matcher.MatcherTestData
import com.wavesplatform.matcher.fixtures.RestartableActor
import com.wavesplatform.matcher.fixtures.RestartableActor.RestartActor
import com.wavesplatform.matcher.market.MatcherActor.OrderAccepted
import com.wavesplatform.matcher.market.OrderBookActor._
import com.wavesplatform.utils.ScorexLogging
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpecLike}
import scorex.transaction.assets.exchange.AssetPair

class OrderBookActrorSpec extends TestKit(ActorSystem("MatcherTest"))
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with ImplicitSender
  with MatcherTestData
  with BeforeAndAfterEach
  with ScorexLogging {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  override protected def beforeEach() = {
    val tp = TestProbe()
    tp.send(StorageExtension(system).journalStorage, InMemoryJournalStorage.ClearJournal)
    tp.expectMsg(akka.actor.Status.Success(""))
    tp.send(StorageExtension(system).snapshotStorage, InMemorySnapshotStorage.ClearSnapshots)
    tp.expectMsg(akka.actor.Status.Success(""))
    super.beforeEach()
  }

  "OrderBookActror" should {
    val pair = AssetPair("BTC".getBytes, "WAVES".getBytes)

    "place buy orders" in {
      val ord1 = buy(pair, 34118, 1583290045643L)
      val ord2 = buy(pair, 34120, 170484969L)
      val ord3 = buy(pair, 34000, 44521418496L)
      val actor = system.actorOf(OrderBookActor.props(pair))

      actor ! ord1
      expectMsg(OrderAccepted(ord1))
      actor ! ord2
      expectMsg(OrderAccepted(ord2))
      actor ! ord3
      expectMsg(OrderAccepted(ord3))

      actor ! GetOrdersRequest
      expectMsg(GetOrdersResponse(Seq(ord2, ord1, ord3)))
    }

    "place sell orders" in {
      val ord1 = sell(pair, 34110, 1583290045643L)
      val ord2 = sell(pair, 34220, 170484969L)
      val ord3 = sell(pair, 34000, 44521418496L)
      val actor = system.actorOf(OrderBookActor.props(pair))

      actor ! ord1
      expectMsg(OrderAccepted(ord1))
      actor ! ord2
      expectMsg(OrderAccepted(ord2))
      actor ! ord3
      expectMsg(OrderAccepted(ord3))

      actor ! GetOrdersRequest
      expectMsg(GetOrdersResponse(Seq(ord3, ord1, ord2)))
    }

    "sell market" in {
      val ord1 = buy(pair, 100, 10)
      val ord2 = sell(pair, 100, 10)
      val actor = system.actorOf(OrderBookActor.props(pair))

      actor ! ord1
      actor ! ord2
      receiveN(2)

      actor ! GetOrdersRequest
      expectMsg(GetOrdersResponse(Seq.empty))
    }

    "place buy and sell order to the order book and preserve it after restart" in {
      val ord1 = buy(pair, 100, 10)
      val ord2 = sell(pair, 150, 15)
      val actor = system.actorOf(Props(new OrderBookActor(pair) with RestartableActor))

      actor ! ord1
      actor ! ord2
      receiveN(2)

      actor ! RestartActor
      actor ! GetOrdersRequest

      expectMsg(GetOrdersResponse(Seq(ord2, ord1)))
    }

    "execute partial market orders and preserve remaining after restart" in {
      val ord1 = buy(pair, 100, 10)
      val ord2 = sell(pair, 100, 15)
      val actor = system.actorOf(Props(new OrderBookActor(pair) with RestartableActor))

      actor ! ord1
      expectMsgType[OrderAccepted]
      actor ! ord2
      expectMsgType[OrderAccepted]

      actor ! RestartActor
      actor ! GetOrdersRequest

      expectMsg(GetOrdersResponse(Seq(ord2.copy(amount = 5))))
    }

    "execute one order fully and other partially and restore after restart" in {
      val actor = system.actorOf(Props(new OrderBookActor(pair) with RestartableActor))
      val ord1 = buy(pair, 100, 10)
      val ord2 = buy(pair, 100, 5)
      val ord3 = sell(pair, 100, 12)

      actor ! ord1
      actor ! ord2
      actor ! ord3
      receiveN(3)

      actor ! RestartActor

      actor ! GetBidOrdersRequest
      expectMsg(GetOrdersResponse(Seq(ord2.copy(amount = 3))))

      actor ! GetAskOrdersRequest
      expectMsg(GetOrdersResponse(Seq.empty))

    }

    "match multiple best orders at once and restore after restart" in {
      val actor = system.actorOf(Props(new OrderBookActor(pair) with RestartableActor))
      val ord1 = sell(pair, 100, 10)
      val ord2 = sell(pair, 100, 5)
      val ord3 = sell(pair, 90, 5)
      val ord4 = buy(pair, 100, 19)

      actor ! ord1
      actor ! ord2
      actor ! ord3
      actor ! ord4
      receiveN(4)

      actor ! RestartActor

      actor ! GetBidOrdersRequest
      expectMsg(GetOrdersResponse(Seq.empty))

      actor ! GetAskOrdersRequest
      expectMsg(GetOrdersResponse(Seq(ord2.copy(amount = 1))))

    }

    "place orders and restart without waiting for responce" in {
      val actor = system.actorOf(Props(new OrderBookActor(pair) with RestartableActor))
      val ord1 = sell(pair, 100, 10)
      val ord2 = buy(pair, 100, 19)

      (1 to 1000).foreach({i =>
        actor ! ord1.copy()
      })
//      actor ! ord1
//      actor ! ord2

      ignoreMsg {
        case GetOrdersResponse(_) => false
        case m => true
      }

      Thread.sleep(1000)
      actor ! RestartActor

      actor ! GetOrdersRequest

      val items = expectMsgType[GetOrdersResponse].orders.map(_.id) //should have size 1000
      println(items)
      //expectMsgGetOrdersResponse(Seq(ord2.copy(amount = 9)))) s

    }
  }

}
