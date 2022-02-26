package com.rodrigo.quispe.vertx_stock_broker.assets

import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonArray
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory

class AssetsRestApi {

  companion object {
    private val logger = LoggerFactory.getLogger(AssetsRestApi::class.java)

    val ASSETS = mutableListOf("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA")

    fun attach(parent: Router) {
      parent.get("/assets")
        .handler { context ->
          val response = JsonArray()
          ASSETS.stream().map { Asset(it) }.forEach(response::add)

          logger.info("PATH {} RESPONDS_WITH {}", context.normalizedPath(), response.encode())
          context.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(response.toBuffer())
        }
    }
  }
}
