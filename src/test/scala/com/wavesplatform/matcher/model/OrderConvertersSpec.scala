package com.wavesplatform.matcher.model

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.PropertyChecks
import play.api.libs.json.{JsError, JsValue, Json}
import scorex.account.PublicKeyAccount
import scorex.crypto.encode.Base58

class OrderConvertersSpec extends PropSpec with PropertyChecks with Matchers {

  val base58Str = "abcd"
  val json: JsValue = Json.parse(s"""
    {
      "sender": "${base58Str}",
      "wrong_sender": "0abcd",
      "wrong_long": "12e",
      "publicKey": "${base58Str}",
      "wrong_publicKey": "0abcd"
    }
    """)

  property("Json Reads Base58") {
    import OrderConverters._

    val sender = (json \ "sender").as[Array[Byte]]
    sender shouldBe Base58.decode(base58Str).get

    (json \ "wrong_sender").validate[Array[Byte]] match {
      case e: JsError =>
        println("Errors: " + JsError.toJson(e).toString())
      case _ => fail("Should be JsError")
    }
  }

  property("Json Reads PublicKeyAccount") {
    import OrderConverters._
    val publicKey = (json \ "publicKey").as[PublicKeyAccount]
    publicKey.bytes shouldBe new PublicKeyAccount(Base58.decode(base58Str).get).bytes

    (json \ "wrong_publicKey").validate[PublicKeyAccount] match {
      case e: JsError =>
        println("Errors: " + JsError.toJson(e).toString())
        e.errors.head._2.head.message shouldBe "error.incorrect.publicKeyAccount"
      case _ => fail("Should be JsError")
    }
  }


}
