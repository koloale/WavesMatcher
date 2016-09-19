package com.wavesplatform.serialization

trait BytesSerializable extends Serializable {

  def bytes: Array[Byte]
}
