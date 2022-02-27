package com.rodrigo.quispe.vertx_stock_broker

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory


class MainVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

  companion object {
    const val PORT = 8888
  }

  override fun start(startPromise: Promise<Void>) {
    vertx.deployVerticle(RestApiVerticle::class.java.name, DeploymentOptions().setInstances(processors()))
      .onFailure {
        logger.error("FAILED_TO_DEPLOY: ", it.message)
      }
      .onSuccess { id ->
        logger.info("DEPLOYED_MAIN: with id: {}", id)
        startPromise.complete()
      }

  }

  private fun processors(): Int = Math.max(1, Runtime.getRuntime().availableProcessors())
}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle())
    .onFailure {
      println("FAILED_TO_DEPLOY: ${it.message}")
    }
    .onSuccess { id ->
      println("DEPLOYED_MAIN: with id: $id")
    }
}
