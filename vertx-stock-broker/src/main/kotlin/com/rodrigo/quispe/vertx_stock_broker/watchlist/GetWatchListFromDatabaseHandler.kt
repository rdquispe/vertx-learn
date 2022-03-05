package com.rodrigo.quispe.vertx_stock_broker.watchlist

import com.rodrigo.quispe.vertx_stock_broker.db.DbResponse
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonArray
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.templates.SqlTemplate
import org.slf4j.LoggerFactory

class GetWatchListFromDatabaseHandler(val db: Pool) : Handler<RoutingContext> {

  private val logger = LoggerFactory.getLogger(GetWatchListFromDatabaseHandler::class.java)

  override fun handle(context: RoutingContext) {
    val accountId = WatchListRestApi.getAccountId(context)

    SqlTemplate.forQuery(db, "SELECT w.asset FROM broker.watchlist w WHERE w.account_id=#{account_id}")
      .mapTo(Row::toJson)
      .execute(hashMapOf("account_id" to accountId).toMap())
      .onFailure { error -> DbResponse.errorHandler(context, error, "WATCHLIST_ACCOUNT_ID") }
      .onSuccess { assets ->
        if (! assets.iterator().hasNext()) {
          DbResponse.notFound(context, "watchlist for accountId $accountId not available!")
          return@onSuccess
        }
        var response = JsonArray()
        assets.forEach(response::add)
        logger.info("PATH {} RESPONDS_WITH {}", context.normalizedPath(), response.encode())
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer())
      }
  }
}
