package com.wavesplatform.matcher

case class Instrument(symbol: String)

object Instruments {
  val values = Seq(Instrument("EUR"), Instrument("GBP"))
}