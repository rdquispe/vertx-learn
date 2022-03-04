package com.rodrigo.quispe.vertx_stock_broker.db

import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

class DbResponse {
  companion object {

    private val logger = LoggerFactory.getLogger(DbResponse::class.java)

    fun errorHandler(context: RoutingContext, error: Throwable, message: String) {
      logger.error("FAILURE: {}", error)
      context.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
        .end(
          JsonObject()
            .put("message", message)
            .put("path", context.normalizedPath()).toBuffer()
        )
    }

    fun notFound(context: RoutingContext, assetParam: String) {
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
  }
}
