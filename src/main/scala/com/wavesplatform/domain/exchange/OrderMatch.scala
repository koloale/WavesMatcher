package com.wavesplatform.domain.exchange

import scala.util.Try

import com.google.common.primitives.{Ints, Longs}
import com.wavesplatform.crypto.EllipticCurveImpl
import com.wavesplatform.domain.account.Account
import com.wavesplatform.domain.transaction.Transaction
import com.wavesplatform.serialization.{BytesSerializable, Deser}
import play.api.libs.json.{JsObject, Json}
import scorex.crypto.encode.Base58

/**
  * Transaction with matched orders generated by Matcher service
  */
case class OrderMatch(order1: WavesOrder, order2: WavesOrder, price: Long, amount: Long, matcherFee: Long, fee: Long,
                      timestamp: Long, signature: Array[Byte]) extends Transaction with BytesSerializable {

  def isValid(previousMatches: Seq[OrderMatch]): Boolean = {
    lazy val order1Transactions = previousMatches.filter { om =>
      (om.order1.signature sameElements order1.signature) || (om.order2.signature sameElements order1.signature)
    }
    lazy val order2Transactions = previousMatches.filter { om =>
      (om.order1.signature sameElements order2.signature) || (om.order2.signature sameElements order2.signature)
    }

    lazy val ordersMatches: Boolean = {
      lazy val priceMatches = if (order1.priceAssetId sameElements order1.receiveAssetId) order1.price >= order2.price
      else order2.price <= order1.price
      (order1.matcher.address == order2.matcher.address) &&
        (order1.spendAssetId sameElements order2.receiveAssetId) &&
        (order2.spendAssetId sameElements order1.receiveAssetId) && priceMatches
    }.ensuring(a => !a || (order1.priceAssetId sameElements order2.priceAssetId))
    lazy val priceIsValid: Boolean = (order1.price == price) || (order2.price == price)
    lazy val amountIsValid: Boolean = {
      val order1Total = order1Transactions.map(_.amount).sum + amount
      val order2Total = order2Transactions.map(_.amount).sum + amount
      (order1Total <= order1.amount) && (order2Total <= order2.amount)
    }
    lazy val matcherFeeIsValid: Boolean = {
      //TODO Matcher takes all his fee on his first match and takes nothing after that
      val o1maxFee = if (order1Transactions.isEmpty) order1.matcherFee else 0
      val o2maxFee = if (order2Transactions.isEmpty) order2.matcherFee else 0
      matcherFee == (o1maxFee + o2maxFee)
    }
    lazy val matcherSignatureIsValid: Boolean =
      EllipticCurveImpl.verify(signature, toSign, order1.matcher.publicKey)

    fee > 0 && amount > 0 && price > 0 && ordersMatches && order1.isValid(timestamp) && order2.isValid(timestamp) &&
      priceIsValid && amountIsValid && matcherFeeIsValid && matcherSignatureIsValid
  }

  lazy val toSign: Array[Byte] = Ints.toByteArray(order1.bytes.length) ++ Ints.toByteArray(order2.bytes.length) ++
    order1.bytes ++ order2.bytes ++ Longs.toByteArray(price) ++ Longs.toByteArray(amount) ++
    Longs.toByteArray(matcherFee) ++ Longs.toByteArray(fee) ++ Longs.toByteArray(timestamp)

  override def bytes: Array[Byte] = toSign ++ signature

  override def json: JsObject = Json.obj(
    "order1" -> order1.json,
    "order2" -> order2.json,
    "price" -> price,
    "amount" -> amount,
    "matcherFee" -> matcherFee,
    "fee" -> fee,
    "timestamp" -> timestamp,
    "signature" -> Base58.encode(signature)
  )

  def balanceChanges(previousMatches: Seq[OrderMatch]): Seq[(Account, (AssetId, Long))] = {
    lazy val order1FirstMatch = !previousMatches.exists { om =>
      (om.order1.signature sameElements order1.signature) || (om.order2.signature sameElements order1.signature)
    }
    lazy val order2FirstMatch = !previousMatches.exists { om =>
      (om.order1.signature sameElements order2.signature) || (om.order2.signature sameElements order2.signature)
    }

    val matcherChange = Seq((order1.matcher, (WavesAssetId, matcherFee - fee)))
    val o1feeChange = if (order1FirstMatch) Seq((order1.sender, (WavesAssetId, -order1.matcherFee))) else Seq()
    val o2feeChange = if (order2FirstMatch) Seq((order2.sender, (WavesAssetId, -order2.matcherFee))) else Seq()

    val exchange = if (order1.priceAssetId sameElements order1.spendAssetId) {
      Seq(
        (order1.sender, (order1.receiveAssetId, amount)),
        (order2.sender, (order1.receiveAssetId, -amount)),
        (order1.sender, (order1.spendAssetId, -amount * price / PriceConstant)),
        (order2.sender, (order1.spendAssetId, +amount * price / PriceConstant))
      )
    } else {
      Seq(
        (order1.sender, (order1.spendAssetId, amount)),
        (order2.sender, (order1.spendAssetId, -amount)),
        (order1.sender, (order1.receiveAssetId, -amount * price / PriceConstant)),
        (order2.sender, (order1.receiveAssetId, +amount * price / PriceConstant))
      )
    }
    o1feeChange ++ o2feeChange ++ matcherChange ++ exchange
  }
}

object OrderMatch extends Deser[OrderMatch] {
  override def parseBytes(bytes: Array[Byte]): Try[OrderMatch] = Try {
    val o1Size = Ints.fromByteArray(bytes.slice(0, 4))
    val o2Size = Ints.fromByteArray(bytes.slice(4, 8))
    val o1 = WavesOrder.parseBytes(bytes.slice(8, 8 + o1Size)).get
    val o2 = WavesOrder.parseBytes(bytes.slice(8 + o1Size, 8 + o1Size + o2Size)).get
    val s = 8 + o1Size + o2Size
    val price = Longs.fromByteArray(bytes.slice(s, s + 8))
    val amount = Longs.fromByteArray(bytes.slice(s + 8, s + 16))
    val matcherFee = Longs.fromByteArray(bytes.slice(s + 16, s + 24))
    val fee = Longs.fromByteArray(bytes.slice(s + 24, s + 32))
    val timestamp = Longs.fromByteArray(bytes.slice(s + 32, s + 40))
    val signature = bytes.slice(s + 40, bytes.length)
    OrderMatch(o1, o2, price, amount, matcherFee, fee, timestamp, signature)
  }
}
