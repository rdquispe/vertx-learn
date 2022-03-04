package com.rodrigo.quispe.vertx_stock_broker.assets

import io.vertx.ext.web.Router
import io.vertx.sqlclient.Pool

class AssetsRestApi {

  companion object {

    val ASSETS = mutableListOf("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA")

    fun attach(parent: Router, pool: Pool) {
      parent.get("/assets").handler(GetAssetsHandler())
      parent.get("/pg/assets").handler(GetAssetsFromDatabaseHandler(pool))
    }
  }
}
