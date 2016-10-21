package com.wavesplatform.matcher.model

import com.wavesplatform.matcher.MatcherTestData
import com.wavesplatform.settings.CoreSettings
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}
import scorex.account.PublicKeyAccount
import scorex.crypto.encode.Base58
import scorex.transaction.assets.exchange.Order
import scorex.utils.NTP

class OrderItemSpec extends PropSpec with PropertyChecks with Matchers with MatcherTestData
  with CoreSettings {

  /*property("OrderItem validate sender adress") {
    val order = validOrderItem()
    order.validate() shouldBe empty
  }*/

  property("Order generator should generate valid orders") {
    forAll(orderGenerator) { order: Order =>
      OrderValidator(order).errors shouldBe empty
    }
  }

  property("Order price validation") {
    forAll(invalidOrderGenerator) { order: Order =>
      whenever(order.price <= 0) {
        OrderValidator(order).errors.toMap() should contain key "maxTimestamp"
      }
    }
  }

  property("Order timestamp validation") {
    forAll(invalidOrderGenerator) { order: Order =>
      val time = NTP.correctedTime()
      whenever(order.maxTimestamp < time || order.maxTimestamp > time + Order.MaxLiveTime) {
        OrderValidator(order).errors.toMap() should contain key "maxTimestamp"
      }
    }
  }

  property("Order amount validation") {
    forAll(invalidOrderGenerator) { order: Order =>
      whenever(order.amount <= 0) {
        OrderValidator(order).errors.toMap() should contain key "amount"
      }
    }
  }

  property("Order matcherFee validation") {
    forAll(invalidOrderGenerator) { order: Order =>
      whenever(order.matcherFee <= 0) {
        OrderValidator(order).errors.toMap() should contain key "matcherFee"
      }
    }
  }

  property("Order signature validation") {
    forAll(orderGenerator, bytes32gen) { (order: Order, bytes: Array[Byte]) =>
      OrderValidator(order).errors shouldBe empty
      OrderValidator(order.copy(sender = new PublicKeyAccount(bytes))).errors.toMap() should contain key "signature"
      OrderValidator(order.copy(matcher = new PublicKeyAccount(bytes))).errors.toMap() should contain key "signature"
      OrderValidator(order.copy(spendAssetId = Array(0: Byte) ++ order.spendAssetId)).errors.toMap() should
        contain key "signature"
      OrderValidator(order.copy(receiveAssetId = Array(0: Byte) ++ order.receiveAssetId)).errors.toMap() should
        contain key "signature"
      OrderValidator(order.copy(price = order.price + 1)).errors.toMap() should contain key "signature"
      OrderValidator(order.copy(amount = order.amount + 1)).errors.toMap() should contain key "signature"
      OrderValidator(order.copy(maxTimestamp = order.maxTimestamp + 1)).errors.toMap() should contain key "signature"
      OrderValidator(order.copy(matcherFee = order.matcherFee + 1)).errors.toMap() should contain key "signature"
      OrderValidator(order.copy(signature = bytes ++ bytes)).errors.toMap() should contain key "signature"
    }
  }

  property("Read Order from json") {
    import OrderJson._
    val json = Json.parse("""
        {
          "sender": "123",
          "matcher": "123",
          "spendAssetId": "string",
          "receiveAssetId": "string",
          "amount": 0,
          "matcherFee": 0,
          "price": 0,
          "maxTimestamp": 0,
          "signature": "signature"
        } """)

    json.validate[Order] match {
      case e: JsError =>
        fail("Error: " + JsError.toJson(e).toString())
      case s: JsSuccess[Order] =>
        val o = s.get
        o.sender shouldBe new PublicKeyAccount(Base58.decode("123").get)
        o.matcher shouldBe new PublicKeyAccount(Base58.decode("123").get)
        o.spendAssetId shouldBe Base58.decode("string").get
        o.receiveAssetId shouldBe Base58.decode("string").get
        o.price shouldBe 0
        o.amount shouldBe 0
        o.matcherFee shouldBe 0
        o.maxTimestamp shouldBe 0
        o.signature shouldBe Base58.decode("signature").get
    }

  }

  property("Read Order without sender and matcher PublicKey") {
    import OrderJson._
    val json = Json.parse(
      """
        {
          "sender": " ",
          "spendAssetId": "string",
          "receiveAssetId": "string",
          "amount": 0,
          "matcherFee": 0,
          "price": 0,
          "maxTimestamp": 0,
          "signature": "signature"
        } """)

    json.validate[Order] match {
      case e: JsError =>
        val paths = e.errors.map(_._1)
        paths should contain allOf (JsPath \ "matcher", JsPath \ "sender")
      case s: JsSuccess[Order] =>
        fail("Should be JsError")
    }
  }

}
