package com.wavesplatform.domain.exchange

import scala.util.Try

import com.google.common.primitives.Longs
import com.wavesplatform.crypto.EllipticCurveImpl
import com.wavesplatform.domain.account.{Account, PrivateKeyAccount, PublicKeyAccount}
import com.wavesplatform.serialization.{BytesSerializable, Deser, JsonSerializable}
import play.api.libs.json.{JsObject, Json}
import scorex.crypto.encode.Base58
import com.wavesplatform.utils.ByteArray


/**
  * Order to matcher service for asset exchange
  */
case class WavesOrder(sender: PublicKeyAccount, matcher: PublicKeyAccount, spendAssetId: AssetId,
                      receiveAssetId: AssetId, price: Long, amount: Long, maxTimestamp: Long, matcherFee: Long,
                      signature: Array[Byte]) extends BytesSerializable with JsonSerializable {

  import WavesOrder._

  /**
    * In what assets is price
    */
  lazy val priceAssetId: AssetId = if (ByteArray.compare(spendAssetId, receiveAssetId) > 0) receiveAssetId
  else spendAssetId

  def isValid(atTime: Long): Boolean = {
    amount > 0 && price > 0 && maxTimestamp - atTime <= MaxLiveTime && atTime <= maxTimestamp &&
      EllipticCurveImpl.verify(signature, toSign, sender.publicKey)
  }

  lazy val toSign: Array[Byte] = sender.publicKey ++ matcher.publicKey ++ spendAssetId ++ receiveAssetId ++
    Longs.toByteArray(price) ++ Longs.toByteArray(amount) ++ Longs.toByteArray(maxTimestamp) ++
    Longs.toByteArray(matcherFee)

  override def bytes: Array[Byte] = toSign ++ signature

  override def json: JsObject = Json.obj(
    "sender" -> sender.address,
    "matcher" -> matcher.address,
    "spendAssetId" -> Base58.encode(spendAssetId),
    "receiveAssetId" -> Base58.encode(receiveAssetId),
    "price" -> price,
    "amount" -> amount,
    "maxTimestamp" -> maxTimestamp,
    "matcherFee" -> matcherFee,
    "signature" -> Base58.encode(signature)
  )

  override def equals(that: scala.Any): Boolean = that match {
    case that: WavesOrder => sender == that.sender &&
      matcher == that.matcher &&
      (spendAssetId sameElements that.spendAssetId) &&
      (receiveAssetId sameElements that.receiveAssetId) &&
      price == that.price &&
      amount == that.amount &&
      maxTimestamp == that.maxTimestamp &&
      matcherFee == that.matcherFee &&
      (signature sameElements that.signature)
    case _ => false
  }


}

object WavesOrder extends Deser[WavesOrder] {
  val MaxLiveTime: Long = 30L * 24L * 60L * 60L * 1000L
  private val AssetIdLength = 32

  def apply(sender: PrivateKeyAccount, matcher: PublicKeyAccount, spendAssetID: Array[Byte],
            receiveAssetID: Array[Byte], price: Long, amount: Long, maxTime: Long, matcherFee: Long): WavesOrder = {
    val unsigned = WavesOrder(sender, matcher, spendAssetID, receiveAssetID, price, amount, maxTime, matcherFee, Array())
    val sig = EllipticCurveImpl.sign(sender, unsigned.toSign)
    WavesOrder(sender, matcher, spendAssetID, receiveAssetID, price, amount, maxTime, matcherFee, sig)
  }

  override def parseBytes(bytes: Array[Byte]): Try[WavesOrder] = Try {
    val sender = new PublicKeyAccount(bytes.slice(0, Account.AddressLength))
    val matcher = new PublicKeyAccount(bytes.slice(Account.AddressLength, 2 * Account.AddressLength))
    val spend = bytes.slice(2 * Account.AddressLength, 2 * Account.AddressLength + AssetIdLength)
    val receive = bytes.slice(2 * Account.AddressLength + AssetIdLength, 2 * Account.AddressLength + 2 * AssetIdLength)
    val longsStart = 2 * Account.AddressLength + 2 * AssetIdLength
    val price = Longs.fromByteArray(bytes.slice(longsStart, longsStart + 8))
    val amount = Longs.fromByteArray(bytes.slice(longsStart + 8, longsStart + 16))
    val maxTimestamp = Longs.fromByteArray(bytes.slice(longsStart + 16, longsStart + 24))
    val matcherFee = Longs.fromByteArray(bytes.slice(longsStart + 24, longsStart + 32))
    val signature = bytes.slice(longsStart + 32, bytes.length)
    WavesOrder(sender, matcher, spend, receive, price, amount, maxTimestamp, matcherFee, signature)
  }
}
