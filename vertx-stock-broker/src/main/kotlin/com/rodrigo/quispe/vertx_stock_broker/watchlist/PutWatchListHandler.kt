package com.rodrigo.quispe.vertx_stock_broker.watchlist

import com.rodrigo.quispe.vertx_stock_broker.watchlist.WatchListRestApi.Companion.getAccountId
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import java.util.UUID

class PutWatchListHandler(private var watchListPerAccount: HashMap<UUID, WatchList>) : Handler<RoutingContext> {

  private val logger = LoggerFactory.getLogger(PutWatchListHandler::class.java)

  override fun handle(context: RoutingContext) {
    val accountId = getAccountId(context)
    val json = context.bodyAsJson
    val watchList = json.mapTo(WatchList::class.java)
    watchListPerAccount[UUID.fromString(accountId)] = watchList
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .end(json.toBuffer())
  }
}
