package com.wavesplatform

import org.scalacheck.{Arbitrary, Gen}

trait ObjectGenerators {

  lazy val nonNegativeLongGen: Gen[Long] = Arbitrary.arbitrary[Long].map(l => Math.abs(l))
  lazy val nonNegativeIntGen: Gen[Int] = Arbitrary.arbitrary[Int].map(l => Math.abs(l))

  lazy val bytesGen: Gen[Array[Byte]] = Gen.listOf(Arbitrary.arbitrary[Byte]).map(_.toArray)

}