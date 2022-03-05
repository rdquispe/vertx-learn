package com.rodrigo.quispe.vertx_stock_broker.watchlist

import com.rodrigo.quispe.vertx_stock_broker.db.DbResponse
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.templates.SqlTemplate
import org.slf4j.LoggerFactory

class PutWatchListFromDatabaseHandler(val db: Pool) : Handler<RoutingContext> {

  private val logger = LoggerFactory.getLogger(PutWatchListFromDatabaseHandler::class.java)

  override fun handle(context: RoutingContext) {
    val accountId = WatchListRestApi.getAccountId(context)
    val json = context.bodyAsJson
    val watchList = json.mapTo(WatchList::class.java)

    val parametersBatch = watchList.assets.map { asset ->
      mapOf<String, Any>("account_id" to accountId, "asset" to asset.name)
    }.toList()

    SqlTemplate.forUpdate(db, "INSERT INTO broker.watchlist (account_id, asset) VALUES (#{account_id}, #{asset})"+
    " ON CONFLICT (account_id, asset) DO NOTHING")
      .executeBatch(parametersBatch)
      .onFailure { error -> DbResponse.errorHandler(context, error, "WATCHLIST_INSERT") }
      .onSuccess { result ->
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end()
      }
  }
}
