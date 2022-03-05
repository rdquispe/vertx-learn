package com.rodrigo.quispe.vertx_stock_broker.watchlist

import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import org.slf4j.LoggerFactory
import java.util.UUID

class WatchListRestApi {

  companion object {

    private val logger = LoggerFactory.getLogger(WatchListRestApi::class.java)

    fun attach(parent: Router, db: Pool) {
      val watchListPerAccount: HashMap<UUID, WatchList> = hashMapOf()
      val path = "/account/watchlist/:accountId"
      parent.get(path).handler(GetWatchListHandler(watchListPerAccount))
      parent.put(path).handler(PutWatchListHandler(watchListPerAccount))
      parent.delete(path).handler(DeleteWatchListHandler(watchListPerAccount))

      val pgPath = "/pg/account/watchlist/:accountId"
      parent.get(pgPath).handler(GetWatchListFromDatabaseHandler(db))
    }

    fun getAccountId(context: RoutingContext): String {
      val accountId = context.pathParam("accountId")
      logger.info("{} for account {}", context.normalizedPath(), accountId)
      return accountId
    }
  }
}
