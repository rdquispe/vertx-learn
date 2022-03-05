package com.rodrigo.quispe.vertx_stock_broker.db

import com.rodrigo.quispe.vertx_stock_broker.config.BrokerConfig
import io.vertx.core.Vertx
import io.vertx.pgclient.PgConnectOptions
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.PoolOptions

class DbPool {

  companion object {
    fun createPgPool(configuration: BrokerConfig, vertx: Vertx): Pool {
      val connectOptions = PgConnectOptions()
        .setHost(configuration.dbConfig.host)
        .setPort(configuration.dbConfig.port)
        .setDatabase(configuration.dbConfig.database)
        .setUser(configuration.dbConfig.user)
        .setPassword(configuration.dbConfig.password)

      val poolOptions = PoolOptions()
        .setMaxSize(4)
      return PgPool.pool(vertx, connectOptions, poolOptions)
    }
  }
}
