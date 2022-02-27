package com.rodrigo.quispe.vertx_stock_broker.watchlist

import com.rodrigo.quispe.vertx_stock_broker.watchlist.WatchListRestApi.Companion.getAccountId
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import java.util.Optional
import java.util.UUID

class GetWatchListHandler(private var watchListPerAccount: HashMap<UUID, WatchList>) : Handler<RoutingContext> {

  private val logger = LoggerFactory.getLogger(GetWatchListHandler::class.java)

  override fun handle(context: RoutingContext) {
    val accountId = getAccountId(context)
    val watchList = Optional.ofNullable(watchListPerAccount[UUID.fromString(accountId)])
    if (watchList.isEmpty) {
      context.response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(
          JsonObject()
            .put("message", "watchlist for account $accountId not available!")
            .put("path", context.normalizedPath()).toBuffer()
        )
      return
    }
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .end(watchList.get().toJsonObject().toBuffer())
  }
}
