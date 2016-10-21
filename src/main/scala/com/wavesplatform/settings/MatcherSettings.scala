package com.wavesplatform.settings

import com.typesafe.config.{Config, ConfigFactory}
import scorex.account.PublicKeyAccount
import scorex.crypto.encode.Base58
import scorex.settings.Settings

class MatcherSettings(override val filename: String) extends Settings{
  val publicKeyStr =  (settingsJSON \ "matcher" \ "publicKey").asOpt[String].getOrElse("")
  val MatcherPublicKey: PublicKeyAccount = new PublicKeyAccount(Base58.decode(publicKeyStr).get)

}

trait CoreSettings {
  implicit val config = ConfigFactory.load()
  implicit val settings = new MatcherSettings("matcher.json")
}