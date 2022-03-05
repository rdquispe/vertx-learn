package com.rodrigo.quispe.vertx_stock_broker.watchlist

import com.rodrigo.quispe.vertx_stock_broker.db.DbResponse
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.templates.SqlTemplate
import org.slf4j.LoggerFactory

class DeleteWatchListDatabaseHandler(val db: Pool) : Handler<RoutingContext> {

  private val logger = LoggerFactory.getLogger(DeleteWatchListDatabaseHandler::class.java)

  override fun handle(context: RoutingContext) {
    val accountId = WatchListRestApi.getAccountId(context)
    SqlTemplate.forUpdate(db, "DELETE FROM broker.watchlist WHERE account_id=#{account_id}")
      .execute(hashMapOf("account_id" to accountId).toMap())
      .onFailure { error -> DbResponse.errorHandler(context, error, "ERROR_DELETED") }
      .onSuccess { result ->
        logger.info("DELETED {}, {}", result.rowCount(), accountId)
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end()
      }
  }
}
