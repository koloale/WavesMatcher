package com.wavesplatform.matcher.model

import scala.util.Try

import com.wavesplatform.settings.MatcherSettings
import io.swagger.annotations.ApiModelProperty
import play.api.libs.json._
import scorex.account.PublicKeyAccount
import scorex.crypto.EllipticCurveImpl
import scorex.crypto.singing.SigningFunctions._
import scorex.transaction.assets.exchange.Order
import scorex.utils.NTP
import skinny.validator._


case object OrderValidator {
  def apply(order: Order)(implicit cfg: MatcherSettings): Validator = {
    import order._
    Validator(
      param("sender" -> sender) is notEmpty,
      param("matcher" -> matcher) is equal(cfg.MatcherPublicKey),
      param("price" -> price) is longMinValue(1),
      param("amount" -> amount) is longMinValue(1),
      param("matcherFee" -> matcherFee) is longMinValue(1),
      param("maxTimestamp" -> maxTimestamp) is liveTimestamp,
      param("signature" -> signature) is length(64) & validSignature(order.toSign, sender.publicKey)
    )
  }

  case class lengthEquals(len: Int) extends ValidationRule {
    def name = "length"
    override def messageParams = Seq(len.toString)
    def isValid(v: Any) = isEmpty(v) || {
      toHasSize(v).map(x => x.size == len)
        .getOrElse(v.toString.length == len)
    }

    override protected def toHasSize(v: Any): Option[{ def size(): Int }] = {
      val x = v.asInstanceOf[{ def size(): Int }]
      try {
        x.size
        Option(x)
      } catch { case e: NoSuchMethodException =>
        val x = v.asInstanceOf[{ def length(): Int }]
        try {
          val xx = new { def size(): Int = x.length }
          xx.size
          Option(xx)
        } catch {
          case e: NoSuchMethodException => None
        }
      }
    }
  }

  case class equal(other: Any) extends ValidationRule {
    def name = "isEqual"
    override def messageParams = Seq(other)

    def isValid(v: Any) = {
      other match {
        case arr: Array[_] if v.isInstanceOf[Array[_]] =>
          v.asInstanceOf[Array[_]].deep == arr.deep
        case o => o == v
      }
    }
  }

  case class validSignature(message: MessageToSign, publicKey: PublicKey) extends ValidationRule {
    def name = "validSignature"
    override def messageParams = Seq(message, publicKey)

    def isValid(v: Any): Boolean = {
      toByteArray(v).exists(EllipticCurveImpl.verify(_, message, publicKey))
    }

    protected def toByteArray(v: Any): Option[Array[Byte]] = {
      Try {
        v.asInstanceOf[Array[Byte]]
      }.toOption
    }
  }

  case object liveTimestamp extends ValidationRule {
    def name = "liveTimestamp"

    def isValid(v: Any): Boolean = {
      isLong(v) && (toLong(v) - NTP.correctedTime() <= Order.MaxLiveTime) && (toLong(v) >= NTP.correctedTime())
    }
  }
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

  case class OrderJson(sender: String, matcher: String, spendAssetId: String, receiveAssetId: String, price: Long,
                       amount: Long, maxTimestamp: Long,  matcherFee: Long, signature: String,
                       @ApiModelProperty(dataType = "java.lang.String") sig: Array[Byte]) {
/*
    @ApiModelProperty(hidden = true)
    lazy val orderItem: Try[Order] = Try {
      val l = Seq(spendAssetId, receiveAssetId).map(_.toUpperCase).sorted
      val pair = (l zip l.tail).head
      val orderType = if (spendAssetId.toUpperCase == pair._1) OrderType.SELL else OrderType.BUY
      new Order(sender, pair, orderType, price, amount, signature)
    }

    @ApiModelProperty(hidden = true)
    lazy val orderTransaction: Order = {
      new Order(new PublicKeyAccount(Base58.decode(sender).get),
        new PublicKeyAccount(Base58.decode(matcher).get),
        Base58.decode(spendAssetId).get,
        Base58.decode(receiveAssetId).get,
        price, amount, maxTimestamp, matcherFee, signature.getBytes)
    }

    @ApiModelProperty(hidden = true)
    lazy val json: JsObject = Json.obj(
      "id" -> Base58.encode("123".getBytes)
    )*/
  }

  object OrderJson {
    implicit val orderJsonFormat = Json.format[OrderJson]

    import OrderConverters._
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Reads._

    def readOrder(sender: PublicKeyAccount, matcher: PublicKeyAccount, spendAssetID: Array[Byte],
              receiveAssetID: Array[Byte], price: Long, amount: Long, maxTime: Long, matcherFee: Long,
              signature:  Array[Byte]): Order = {
      Order(sender, matcher, spendAssetID, receiveAssetID, price, amount, maxTime, matcherFee, signature)
    }

    implicit val orderReads: Reads[Order] = (
      (JsPath \ "sender").read[PublicKeyAccount] and
        (JsPath \ "matcher").read[PublicKeyAccount] and
        (JsPath \ "spendAssetId").read[Array[Byte]] and
        (JsPath \ "receiveAssetId").read[Array[Byte]] and
        (JsPath \ "price").read[Long] and
        (JsPath \ "amount").read[Long] and
        (JsPath \ "maxTimestamp").read[Long] and
        (JsPath \ "matcherFee").read[Long] and
        (JsPath \ "signature").read[Array[Byte]]
      ) (readOrder _)

    /*implicit val orderJsonWrites: Writes[IssueRequest] = (
      (JsPath \ "sender").write[String] and
        (JsPath \ "assetIdOpt").writeNullable[String] and
        (JsPath \ "name").write[String] and
        (JsPath \ "description").write[String] and
        (JsPath \ "quantity").write[Long] and
        (JsPath \ "decimals").write[Byte] and
        (JsPath \ "reissuable").write[Boolean] and
        (JsPath \ "fee").write[Long]
      ) (unlift(IssueRequest.unapply))


    implicit val paymentReads: Reads[IssueRequest] = (
      (JsPath \ "sender").read[String] and
        (JsPath \ "assetIdOpt").readNullable[String] and
        (JsPath \ "name").read[String] and
        (JsPath \ "description").read[String] and
        (JsPath \ "quantity").read[Long] and
        (JsPath \ "decimals").read[Byte] and
        (JsPath \ "reissuable").read[Boolean] and
        (JsPath \ "fee").read[Long]
      ) (IssueRequest.apply _)*/
  }