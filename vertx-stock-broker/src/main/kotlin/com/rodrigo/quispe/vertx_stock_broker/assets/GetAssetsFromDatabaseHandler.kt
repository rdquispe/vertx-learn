package com.rodrigo.quispe.vertx_stock_broker.assets

import com.rodrigo.quispe.vertx_stock_broker.db.DbResponse
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonArray
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import org.slf4j.LoggerFactory

class GetAssetsFromDatabaseHandler(val db: Pool) : Handler<RoutingContext> {

  private val logger = LoggerFactory.getLogger(GetAssetsFromDatabaseHandler::class.java)

  override fun handle(context: RoutingContext) {

    db.query("SELECT a.value FROM broker.assets a")
      .execute()
      .onFailure { error -> DbResponse.errorHandler(context, error, "FAIL_GET_ASSET_FROM_DATABASE") }
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
