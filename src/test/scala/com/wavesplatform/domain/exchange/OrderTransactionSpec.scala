package com.wavesplatform.domain.exchange

import com.wavesplatform.TransactionGen
import com.wavesplatform.domain.account.PublicKeyAccount
import com.wavesplatform.utils.NTP
import org.scalatest._
import org.scalatest.prop.PropertyChecks
import org.scalacheck.Prop.forAll

class OrderTransactionSpec extends PropSpec with PropertyChecks with Matchers with TransactionGen {

  property("Order transaction serialization roundtrip") {
    forAll(orderGenerator) { order: WavesOrder =>
      val recovered = WavesOrder.parseBytes(order.bytes).get
      recovered.bytes shouldEqual order.bytes
    }
  }

  property("Order generator should generate valid orders") {
    forAll(orderGenerator) { order: WavesOrder =>
      order.isValid(NTP.correctedTime()) shouldBe true
    }
  }

  property("Order timestamp validation") {
    forAll(invalidOrderGenerator) { order: WavesOrder =>
      val isValid = order.isValid(NTP.correctedTime())
      val time = NTP.correctedTime()
      whenever(order.maxTimestamp < time || order.maxTimestamp > time + WavesOrder.MaxLiveTime) {
        isValid shouldBe false
      }
    }
  }

  property("Order amount validation") {
    forAll(invalidOrderGenerator) { order: WavesOrder =>
      whenever(order.amount <= 0) {
        order.isValid(NTP.correctedTime()) shouldBe false
      }
    }
  }

  property("Order matcherFee validation") {
    forAll(invalidOrderGenerator) { order: WavesOrder =>
      whenever(order.matcherFee <= 0) {
        order.isValid(NTP.correctedTime()) shouldBe false
      }
    }
  }

  property("Order price validation") {
    forAll(invalidOrderGenerator) { order: WavesOrder =>
      whenever(order.price <= 0) {
        order.isValid(NTP.correctedTime()) shouldBe false
      }
    }
  }

  property("Order signature validation") {
    forAll(orderGenerator, bytes32gen) { (order: WavesOrder, bytes: Array[Byte]) =>
      order.isValid(NTP.correctedTime()) shouldBe true
      order.copy(sender = new PublicKeyAccount(bytes)).isValid(NTP.correctedTime()) shouldBe false
      order.copy(matcher = new PublicKeyAccount(bytes)).isValid(NTP.correctedTime()) shouldBe false
      order.copy(spendAssetId = Array(0: Byte) ++ order.spendAssetId).isValid(NTP.correctedTime()) shouldBe false
      order.copy(receiveAssetId = Array(0: Byte) ++ order.receiveAssetId).isValid(NTP.correctedTime()) shouldBe false
      order.copy(price = order.price + 1).isValid(NTP.correctedTime()) shouldBe false
      order.copy(amount = order.amount + 1).isValid(NTP.correctedTime()) shouldBe false
      order.copy(maxTimestamp = order.maxTimestamp + 1).isValid(NTP.correctedTime()) shouldBe false
      order.copy(matcherFee = order.matcherFee + 1).isValid(NTP.correctedTime()) shouldBe false
      order.copy(signature = bytes ++ bytes).isValid(NTP.correctedTime()) shouldBe false
    }
  }

}
