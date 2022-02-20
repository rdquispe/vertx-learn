package com.rodrigo.quispe.vertx_stock_broker

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory

class AssetsRestApi {

  companion object {
    private val logger = LoggerFactory.getLogger(AssetsRestApi::class.java)

    fun attach(parent: Router) {
      parent.get("/assets")
        .handler { context ->
          val response = JsonArray()
          response
            .add(JsonObject().put("symbol", "AAPL"))
            .add(JsonObject().put("symbol", "AMZN"))
            .add(JsonObject().put("symbol", "NFLX"))
            .add(JsonObject().put("symbol", "TSLA"))

          logger.info("PATH {} RESPONDS_WITH {}", context.normalizedPath(), response.encode())
          context.response().end(response.toBuffer())
        }
    }
  }
}
