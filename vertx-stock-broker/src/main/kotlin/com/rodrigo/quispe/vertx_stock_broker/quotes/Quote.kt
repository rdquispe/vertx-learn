package com.rodrigo.quispe.vertx_stock_broker.quotes

import com.rodrigo.quispe.vertx_stock_broker.assets.Asset
import io.vertx.core.json.JsonObject
import java.math.BigDecimal

data class Quote(
  val asset: Asset,
  val bid: BigDecimal,
  val ask: BigDecimal,
  val lastPrice: BigDecimal,
  val volume: BigDecimal
) {
  fun toJsonObject(): JsonObject = JsonObject.mapFrom(this)
}
