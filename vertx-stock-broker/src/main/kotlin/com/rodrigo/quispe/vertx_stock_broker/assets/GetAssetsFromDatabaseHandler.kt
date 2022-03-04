package com.rodrigo.quispe.vertx_stock_broker.assets

import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.pgclient.PgPool
import org.slf4j.LoggerFactory

class GetAssetsFromDatabaseHandler(val db: PgPool) : Handler<RoutingContext> {

  private val logger = LoggerFactory.getLogger(GetAssetsFromDatabaseHandler::class.java)

  override fun handle(context: RoutingContext) {

    db.query("SELECT a.value FROM broker.assets a")
      .execute()
      .onFailure { error ->
        logger.error("FAILURE: {}", error)
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .end(
            JsonObject()
              .put("message", "FAIL_GET_ASSET_FROM_DATABASE")
              .put("path", context.normalizedPath()).toBuffer()
          )
      }
      .onSuccess { result ->
        val response = JsonArray()
        result.forEach { row -> response.add(row.getValue("value")) }
        logger.info("SUCCESS: {} {}", context.normalizedPath(), response.encode())
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer())
      }
  }
}
