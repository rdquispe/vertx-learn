package com.rodrigo.quispe.vertx_stock_broker.quotes

import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import java.util.Optional

class GetQuoteHandler(var cachedQuotes: HashMap<String, Quote>) : Handler<RoutingContext> {

  private val logger = LoggerFactory.getLogger(GetQuoteHandler::class.java)

  override fun handle(context: RoutingContext) {
    val assetParam = context.pathParam("asset")
    logger.info("ASSET_PARAMETER: {}", assetParam)

    val maybeQuote = Optional.ofNullable(cachedQuotes[assetParam])
    if (maybeQuote.isEmpty) {
      context.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(
          JsonObject()
            .put("message", "quote for asset $assetParam not available!")
            .put("path", context.normalizedPath()).toBuffer()
        )
      return
    }
    val response = maybeQuote.get().toJsonObject()
    logger.info("PATH {} RESPONDS_WITH {}", context.normalizedPath(), response.encode())
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .end(response.toBuffer())
  }
}
