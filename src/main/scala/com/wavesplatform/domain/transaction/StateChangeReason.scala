package com.wavesplatform.domain.transaction

import com.wavesplatform.serialization.BytesSerializable

/**
  * reason to change account balance
  */
trait StateChangeReason extends BytesSerializable {

  val signature: Array[Byte]
}
