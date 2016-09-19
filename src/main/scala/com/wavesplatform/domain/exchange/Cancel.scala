package com.wavesplatform.domain.exchange

import com.wavesplatform.domain.account.PublicKeyAccount

/**
  * Cancel transaciton to be sent to matcher
  */
case class Cancel(spendAddress: PublicKeyAccount, orderId: Array[Byte], signature: Array[Byte])
