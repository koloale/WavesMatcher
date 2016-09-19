package com.wavesplatform.matcher

import java.util.UUID

import com.wavesplatform.matcher.OrderType.OrderType
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import scorex.crypto.encode.Base58

import scala.util.Try

/*sealed trait Order {
  val orderType:OrderType
  val instrument: Instrument
  val price: Double
  val quantity: Double
}*/

case class OrderItem(id: String, orderType: OrderType, instrument: Instrument, price: Double, quantity: Double) {
  lazy val isValid = true
}

object Buy {
  def apply(clientId: String, instrument: Instrument, price: Double, quantity: Double) =
    OrderItem(clientId, OrderType.BUY,  instrument, price, quantity)
}


object Sell {
  def apply(clientId: String, instrument: Instrument, price: Double, quantity: Double) =
    OrderItem(clientId, OrderType.SELL,  instrument, price, quantity)
}

case class OrderJS(spendAddress: String, matcherAddress: String, spendTokenID: String, receiveTokenID: String,
                   price: Int, amount: Long, signature: String) {

  lazy val order: Try[OrderItem] = Try {
    OrderItem(UUID.randomUUID().toString, OrderType.BUY, Instrument("BTC"), price, amount)
  }
  /*lazy val order: Try[Order] = Try {
    val add = new PublicKeyAccount(Base58.decode(spendAddress).get)
    val matcher = new PublicKeyAccount(Base58.decode(matcherAddress).get)
    val spendToken = Base58.decode(spendTokenID).get
    val receiveToken = Base58.decode(receiveTokenID).get
    val sig = Base58.decode(signature).get
    Order(add, matcher, spendToken, receiveToken, price, amount, sig)
  }*/

}

object OrderJS {
  implicit val paymentWrites: Writes[OrderJS] =
    Json.writes[OrderJS]

  implicit val paymentReads: Reads[OrderJS] =
    Json.reads[OrderJS]

}
