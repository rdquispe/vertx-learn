package com.rodrigo.quispe.vertx_stock_broker.quotes

import com.rodrigo.quispe.vertx_stock_broker.db.DbResponse
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.templates.SqlTemplate
import org.slf4j.LoggerFactory

class GetQuoteFromDatabaseHandler(val db: Pool) : Handler<RoutingContext> {

  private val logger = LoggerFactory.getLogger(GetQuoteFromDatabaseHandler::class.java)

  override fun handle(context: RoutingContext) {
    val assetParam = context.pathParam("asset")
    logger.info("ASSET_PARAMETER: {}", assetParam)

    SqlTemplate.forQuery(db, "SELECT q.asset, q.bid, q.ask, q.last_price, q.volume FROM broker.quotes q WHERE asset=#{asset}")
      .mapTo(QuoteEntity::class.java)
      .execute(hashMapOf("asset" to assetParam).toMap())
      .onFailure { error -> DbResponse.errorHandler(context, error, "quote for asset $assetParam not available!") }
      .onSuccess { quotes ->

        if(!quotes.iterator().hasNext()) {
          DbResponse.notFound(context, "quote for asset $assetParam not available!")
          return@onSuccess
        }

        val response = quotes.iterator().next().toJsonObject()
        logger.info("PATH {} RESPONDS_WITH {}", context.normalizedPath(), response.encode())
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer())
      }
  }
}
