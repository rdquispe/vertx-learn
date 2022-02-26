package com.rodrigo.quispe.vertx_stock_broker.watchlist

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
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
        val accountId = context.pathParam("accountId")
        logger.info("{} for account {}", context.normalizedPath(), accountId)
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
        context.response().end(watchList.get().toJsonObject().toBuffer())
      }
      parent.put(path).handler { context ->
        val accountId = context.pathParam("accountId")
        logger.info("{} for account {}", context.normalizedPath(), accountId)
        val json = context.bodyAsJson
        val watchList = json.mapTo(WatchList::class.java)
        watchListPerAccount[UUID.fromString(accountId)] = watchList
        context.response().end(json.toBuffer())
      }
      parent.delete(path).handler { context ->

      }
    }
  }
}
