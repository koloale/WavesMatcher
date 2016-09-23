package com.wavesplatform.matcher.model

import java.util.UUID

import scala.util.Try

import com.wavesplatform.matcher.AssetPair
import com.wavesplatform.matcher.model.OrderType.OrderType
import play.api.libs.json._

object OrderType extends Enumeration {
  type OrderType = Value
  val BUY, SELL = Value
}


case class OrderItem(id: String, senderAddress: String, assetPair: AssetPair, orderType: OrderType,
                     price: Int, amount: Long, signature: String) {

}

/*case class Order(id: String, spendAddress: String, matcherAddress: String, sellAssetId: AssetId, buyAssetId: String,
                 price: Int, amount: Long, signature: String) {

  lazy val order: Try[OrderItem] = Try {
    OrderItem(UUID.randomUUID().toString, OrderType.BUY, "BTC", price, amount)
  }

  def isValid = true

  lazy val order: Try[Order] = Try {
  val add = new PublicKeyAccount(Base58.decode(spendAddress).get)
  val matcher = new PublicKeyAccount(Base58.decode(matcherAddress).get)
  val spendToken = Base58.decode(spendTokenID).get
  val receiveToken = Base58.decode(receiveTokenID).get
  val sig = Base58.decode(signature).get
  Order(add, matcher, spendToken, receiveToken, price, amount, sig)
}
}*/

case class OrderJson(senderAddress: String, matcherAddress: String, spendAssetId: String, receiveAssetId: String,
                 price: Int, amount: Long, signature: String) {
  lazy val orderItem: Try[OrderItem] = Try {
    val l = Seq(spendAssetId, receiveAssetId).map(_.toUpperCase).sorted
    val pair = (l zip l.tail).head
    val orderType = if (spendAssetId == pair._1) OrderType.SELL else OrderType.BUY
    OrderItem(UUID.randomUUID().toString, senderAddress, pair, orderType,
      price, amount, signature)
  }
}
object OrderJson {
  implicit val orderJsonFormat = Json.format[OrderJson]
}