package com.wavesplatform

import com.wavesplatform.crypto.EllipticCurveImpl
import com.wavesplatform.domain.account.PrivateKeyAccount
import com.wavesplatform.domain.exchange.{OrderMatch, WavesOrder}
import com.wavesplatform.utils.NTP
import org.scalacheck.{Arbitrary, Gen}

trait TransactionGen {

  val bytes32gen: Gen[Array[Byte]] = Gen.listOfN(32, Arbitrary.arbitrary[Byte]).map(_.toArray)
  val bytes64gen: Gen[Array[Byte]] = Gen.listOfN(64, Arbitrary.arbitrary[Byte]).map(_.toArray)

  val accountGen: Gen[PrivateKeyAccount] = bytes32gen.map(seed => new PrivateKeyAccount(seed))
  val positiveLongGen: Gen[Long] = Gen.choose(1, Long.MaxValue)

  /*val paymentGenerator: Gen[PaymentTransaction] = for {
    amount: Long <- Gen.choose(0, Long.MaxValue)
    fee: Long <- positiveLongGen
    timestamp: Long <- positiveLongGen
    sender: PrivateKeyAccount <- accountGen
    recepient: PrivateKeyAccount <- accountGen
  } yield PaymentTransaction(sender, recepient, amount, fee, timestamp)
*/
  val orderGenerator: Gen[WavesOrder] = for {
    sender: PrivateKeyAccount <- accountGen
    matcher: PrivateKeyAccount <- accountGen
    spendAssetID: Array[Byte] <- bytes32gen
    receiveAssetID: Array[Byte] <- bytes32gen
    price: Long <- positiveLongGen
    amount: Long <- positiveLongGen
    maxtTime: Long <- Gen.choose(10000L, WavesOrder.MaxLiveTime).map(_ + NTP.correctedTime())
    matcherFee: Long <- positiveLongGen
  } yield WavesOrder(sender, matcher, spendAssetID, receiveAssetID, price, amount, maxtTime, matcherFee)

  val invalidOrderGenerator: Gen[WavesOrder] = for {
    sender: PrivateKeyAccount <- accountGen
    matcher: PrivateKeyAccount <- accountGen
    spendAssetID: Array[Byte] <- bytes32gen
    receiveAssetID: Array[Byte] <- bytes32gen
    price: Long <- Arbitrary.arbitrary[Long]
    amount: Long <- Arbitrary.arbitrary[Long]
    maxtTime: Long <- Arbitrary.arbitrary[Long]
    matcherFee: Long <- Arbitrary.arbitrary[Long]
  } yield WavesOrder(sender, matcher, spendAssetID, receiveAssetID, price, amount, maxtTime, matcherFee)

  val orderMatchGenerator: Gen[OrderMatch] = for {
    sender1: PrivateKeyAccount <- accountGen
    sender2: PrivateKeyAccount <- accountGen
    matcher: PrivateKeyAccount <- accountGen
    spendAssetID: Array[Byte] <- bytes32gen
    receiveAssetID: Array[Byte] <- bytes32gen
    price: Long <- positiveLongGen
    amount1: Long <- positiveLongGen
    amount2: Long <- positiveLongGen
    matchedAmount: Long <- Gen.choose(1L, Math.min(amount1, amount2))
    maxtTime: Long <- Gen.choose(10000L, WavesOrder.MaxLiveTime).map(_ + NTP.correctedTime())
    timestamp: Long <- positiveLongGen
    matcherFee: Long <- positiveLongGen
    fee: Long <- positiveLongGen
  } yield {
    val o1 = WavesOrder(sender1, matcher, spendAssetID, receiveAssetID, price, amount1, maxtTime, matcherFee)
    val o2 = WavesOrder(sender2, matcher, receiveAssetID, spendAssetID, price, amount2, maxtTime, matcherFee)
    val unsigned = OrderMatch(o1, o2, price, matchedAmount, matcherFee * 2, fee, timestamp, Array())
    val sig = EllipticCurveImpl.sign(matcher, unsigned.toSign)
    OrderMatch(o1, o2, price, matchedAmount, matcherFee * 2, fee, timestamp, sig)
  }


}
