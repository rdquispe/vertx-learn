package com.rodrigo.quispe.vertx_stock_broker.watchlist

import com.rodrigo.quispe.vertx_stock_broker.db.DbResponse
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.SqlConnection
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

    // 1 - Transaction
    db.withConnection { client ->
      // 2 - Delete for account id
      SqlTemplate.forUpdate(client, "DELETE FROM broker.watchlist w WHERE w.account_id=#{account_id}")
        .execute(hashMapOf("account_id" to accountId).toMap())
        .onFailure { error -> DbResponse.errorHandler(context, error, "FAILED_CLEAR") }
        .compose{ deletionDone ->
          addAllForAccountId(client, parametersBatch, context)
        }
        .onFailure { error -> DbResponse.errorHandler(context, error, "FAILED_UPDATE") }
        .onSuccess { result ->
          // 3 - Both success
          context.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end()
        }
    }
  }

  private fun addAllForAccountId(client: SqlConnection, parametersBatch: List<Map<String, Any>>, context: RoutingContext) =
    SqlTemplate.forUpdate(client, "INSERT INTO broker.watchlist (account_id, asset) VALUES (#{account_id}, #{asset})" +
      " ON CONFLICT (account_id, asset) DO NOTHING")
      .executeBatch(parametersBatch)
      .onFailure { error -> DbResponse.errorHandler(context, error, "WATCHLIST_INSERT") }
}
