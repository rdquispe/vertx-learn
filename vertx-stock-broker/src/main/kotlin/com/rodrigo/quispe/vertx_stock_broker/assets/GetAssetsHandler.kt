package com.rodrigo.quispe.vertx_stock_broker.assets

import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonArray
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

class GetAssetsHandler : Handler<RoutingContext> {

  private val logger = LoggerFactory.getLogger(GetAssetsHandler::class.java)

  override fun handle(context: RoutingContext) {
    val response = JsonArray()
    AssetsRestApi.ASSETS.stream().map { Asset(it) }.forEach(response::add)

    logger.info("PATH {} RESPONDS_WITH {}", context.normalizedPath(), response.encode())
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .putHeader("my-header", "my-value")
      .end(response.toBuffer())
  }
}
