package com.rodrigo.quispe.vertx_stock_broker

import com.rodrigo.quispe.vertx_stock_broker.bd.migration.FlywayMigration
import com.rodrigo.quispe.vertx_stock_broker.config.ConfigLoader
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory


class MainVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    vertx.deployVerticle(VersionInfoVerticle::class.java.name)
      .onFailure(startPromise::fail)
      .onSuccess { id -> logger.info("DEPLOYED: {} with id: {}", VersionInfoVerticle::class.java.name, id) }
      .compose { next -> migrateDatabase() }
      .compose { next -> deployRestApiVerticle(startPromise) }
  }

  private fun migrateDatabase(): Future<Void> {

    return ConfigLoader.load(vertx)
      .compose { config ->
        FlywayMigration.migrate(vertx, config.dbConfig)
      }
  }

  private fun deployRestApiVerticle(startPromise: Promise<Void>) =
    vertx.deployVerticle(RestApiVerticle::class.java.name, DeploymentOptions().setInstances(halfProcessors()))
      .onFailure {
        logger.error("FAILED_TO_DEPLOY: ", it.cause)
      }
      .onSuccess { id ->
        logger.info("DEPLOYED: {} with id: {}", RestApiVerticle::class.java.name, id)
        startPromise.complete()
      }

  private fun processors(): Int = Math.max(1, Runtime.getRuntime().availableProcessors())

  private fun halfProcessors() = processors() / 2
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
