package com.wavesplatform.matcher

import java.util.UUID

import com.wavesplatform.matcher.model.{OrderItem, OrderType}

trait MatcherTestData {
  def buy(pair: AssetPair, price: Int, amount: Long) =
    OrderItem(UUID.randomUUID().toString, "wallet1", pair, OrderType.BUY, price, amount, "sign")
  def sell(pair: AssetPair, price: Int, amount: Long) =
    OrderItem(UUID.randomUUID().toString, "wallet1", pair, OrderType.SELL, price, amount, "sign")
}
