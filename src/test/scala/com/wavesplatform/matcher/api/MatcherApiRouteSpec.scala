package com.wavesplatform.matcher.api

import akka.actor.ActorRef
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.{TestActor, TestProbe}

import com.wavesplatform.matcher.MatcherTestData
import com.wavesplatform.matcher.market.MatcherActor.OrderAccepted
import org.scalatest._
import scorex.transaction.assets.exchange.Order

class MatcherApiRouteSpec extends WordSpec with BeforeAndAfterEach with Matchers with ScalatestRouteTest
  with MatcherTestData {

  "Matcher Api" should {
    val probe = TestProbe()
    val api = MatcherApiRoute(probe.ref)
    "place buy order request" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`,
        """
           {
             "price":11,
             "spendAssetId": "waves",
             "signature": "string",
             "amount": 10,
             "matcher": "string",
             "receiveAssetId": "usd",
             "sender": "string"
           }
        """)

      probe.setAutoPilot(new TestActor.AutoPilot {
        def run(sender: ActorRef, msg: Any): TestActor.AutoPilot =
          msg match {
            case order: Order => sender.tell(OrderAccepted(order), probe.ref); TestActor.KeepRunning
          }
      })
      Post("/matcher/orders/place", requestEntity) ~> api.route ~> check {
        response.status should be(StatusCodes.OK)
      }
    }
  }


}
