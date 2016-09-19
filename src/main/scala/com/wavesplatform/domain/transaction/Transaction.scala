package com.wavesplatform.domain.transaction

import com.wavesplatform.serialization.JsonSerializable


/**
  * A transaction is an atomic state modifier
  */

trait Transaction extends StateChangeReason with JsonSerializable {
  val fee: Long

  val timestamp: Long

}
