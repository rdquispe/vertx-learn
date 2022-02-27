package com.rodrigo.quispe.vertx_stock_broker.assets

import io.vertx.ext.web.Router

class AssetsRestApi {

  companion object {

    val ASSETS = mutableListOf("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA")

    fun attach(parent: Router) {
      parent.get("/assets").handler(GetAssetsHandler())
    }
  }
}
