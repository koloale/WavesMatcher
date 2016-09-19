package com.wavesplatform.serialization

import play.api.libs.json.JsObject

trait JsonSerializable {

  def json: JsObject
}
