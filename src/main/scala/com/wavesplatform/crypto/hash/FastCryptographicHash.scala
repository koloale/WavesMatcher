package com.wavesplatform.crypto.hash

import com.typesafe.config.ConfigFactory
import scorex.crypto.hash.CryptographicHash._
import com.wavesplatform.utils._
import scala.util.Try

import scorex.crypto.hash.{Blake256, CryptographicHash}

/**
 * Fast and secure hash function
 */
object FastCryptographicHash extends CryptographicHash {

  private val hf: CryptographicHash = Try(ConfigFactory.load().getConfig("scorex").getString("fastHash"))
    .flatMap(s => objectFromString[CryptographicHash](s)).getOrElse(Blake256)

  override val DigestSize: Int = hf.DigestSize

  override def hash(in: Message): Digest = hf.hash(in)

}
