package com.wavesplatform.matcher

import java.util.UUID

import com.wavesplatform.settings.CoreSettings
import org.scalacheck.{Arbitrary, Gen}
import scorex.account.{PrivateKeyAccount, PublicKeyAccount}
import scorex.crypto.encode.Base58
import scorex.transaction.assets.exchange.{AssetPair, Order, OrderType}
import scorex.utils.NTP

trait MatcherTestData extends CoreSettings {
  def buy(pair: AssetPair, price: Int, amount: Long) =
    orderGenerator.sample.get.copy(spendAssetId = pair.asset2, receiveAssetId = pair.asset1,
      price = price, amount = amount)
  def sell(pair: AssetPair, price: Int, amount: Long) =
    orderGenerator.sample.get.copy(spendAssetId = pair.asset1, receiveAssetId = pair.asset2,
      price = price, amount = amount)

  val bytes32gen: Gen[Array[Byte]] = Gen.listOfN(32, Arbitrary.arbitrary[Byte]).map(_.toArray)
  val accountGen: Gen[PrivateKeyAccount] = bytes32gen.map(seed => new PrivateKeyAccount(seed))
  val positiveLongGen: Gen[Long] = Gen.choose(1, Long.MaxValue)

  val orderGenerator: Gen[Order] = for {
    sender: PrivateKeyAccount <- accountGen
    spendAssetID: Array[Byte] <- bytes32gen
    receiveAssetID: Array[Byte] <- bytes32gen
    price: Long <- positiveLongGen
    amount: Long <- positiveLongGen
    maxtTime: Long <- Gen.choose(10000L, Order.MaxLiveTime).map(_ + NTP.correctedTime())
    matcherFee: Long <- positiveLongGen
  } yield Order(sender, new PublicKeyAccount(settings.MatcherPublicKey.publicKey), spendAssetID, receiveAssetID, price,
    amount,
    maxtTime,
    matcherFee)

  val invalidOrderGenerator: Gen[Order] = for {
    sender: PrivateKeyAccount <- accountGen
    matcher: PrivateKeyAccount <- accountGen
    spendAssetID: Array[Byte] <- bytes32gen
    receiveAssetID: Array[Byte] <- bytes32gen
    price: Long <- Arbitrary.arbitrary[Long]
    amount: Long <- Arbitrary.arbitrary[Long]
    maxtTime: Long <- Arbitrary.arbitrary[Long]
    matcherFee: Long <- Arbitrary.arbitrary[Long]
  } yield Order(sender, matcher, spendAssetID, receiveAssetID, price, amount, maxtTime, matcherFee)

  def validOrder(): Order = {
    orderGenerator.sample.get
  }

}
