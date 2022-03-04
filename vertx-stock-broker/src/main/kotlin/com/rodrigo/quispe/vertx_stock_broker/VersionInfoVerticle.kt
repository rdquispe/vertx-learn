package com.rodrigo.quispe.vertx_stock_broker

import com.rodrigo.quispe.vertx_stock_broker.config.ConfigLoader
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.LoggerFactory

class VersionInfoVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(VersionInfoVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess { configuration ->
        logger.info("CURRENY_APPLICATION_IS: {}", configuration.version)
        startPromise.complete()
      }
  }
}
