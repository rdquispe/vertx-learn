package com.rodrigo.quispe.vertx_stock_broker.watchlist

import com.rodrigo.quispe.vertx_stock_broker.assets.Asset
import io.vertx.core.json.JsonObject

data class WatchList(
  val assets: List<Asset>
) {

  constructor() : this(listOf())

  fun toJsonObject(): JsonObject = JsonObject.mapFrom(this)
}
