package com.rodrigo.quispe.vertx_stock_broker.watchlist

import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import java.util.Optional
import java.util.UUID

class WatchListRestApi {

  companion object {

    private val logger = LoggerFactory.getLogger(WatchListRestApi::class.java)

    fun attach(parent: Router) {
      val watchListPerAccount: HashMap<UUID, WatchList> = hashMapOf()
      val path = "/account/watchlist/:accountId"
      parent.get(path).handler { context ->
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
          return@handler
        }
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(watchList.get().toJsonObject().toBuffer())
      }
      parent.put(path).handler { context ->
        val accountId = getAccountId(context)
        val json = context.bodyAsJson
        val watchList = json.mapTo(WatchList::class.java)
        watchListPerAccount[UUID.fromString(accountId)] = watchList
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(json.toBuffer())
      }
      parent.delete(path).handler { context ->
        val accountId = getAccountId(context)
        val deleted = watchListPerAccount.remove(UUID.fromString(accountId))
        logger.info("DELETED: {}, REMAINING: {}", deleted, watchListPerAccount.values)
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(deleted?.toJsonObject()?.toBuffer())
      }
    }

    private fun getAccountId(context: RoutingContext) : String {
      val accountId = context.pathParam("accountId")
      logger.info("{} for account {}", context.normalizedPath(), accountId)
      return accountId
    }
  }
}
