package com.rodrigo.quispe.vertx_stock_broker

import com.rodrigo.quispe.vertx_stock_broker.assets.AssetsRestApi
import com.rodrigo.quispe.vertx_stock_broker.config.BrokerConfig
import com.rodrigo.quispe.vertx_stock_broker.config.ConfigLoader
import com.rodrigo.quispe.vertx_stock_broker.db.DbPool
import com.rodrigo.quispe.vertx_stock_broker.quotes.QuotesRestApi
import com.rodrigo.quispe.vertx_stock_broker.watchlist.WatchListRestApi
import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import org.slf4j.LoggerFactory

class RestApiVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(RestApiVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {

    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess { configuration ->
        logger.info("Retrived Configuration: {}", configuration.toString())
        startHttpServerAndAttachRoutes(startPromise, configuration)
      }
  }

  private fun startHttpServerAndAttachRoutes(startPromise: Promise<Void>, configuration: BrokerConfig) {
    // Postgres Client
    val db = DbPool.createPgPool(configuration, vertx)

    val restApi = Router.router(vertx)
    restApi.route()
      .handler(BodyHandler.create())
      .failureHandler(handleFailure())
    AssetsRestApi.attach(restApi, db)
    QuotesRestApi.attach(restApi, db)
    WatchListRestApi.attach(restApi, db)

    vertx
      .createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler { error -> logger.error("HTTP_ERROR_SERVER", error) }
      .listen(configuration.serverPort) { http ->
        if (http.succeeded()) {
          startPromise.complete()
          logger.info("HTTP server started on port {}", configuration.serverPort)
        } else {
          startPromise.fail(http.cause());
        }
      }
  }

  private fun handleFailure(): Handler<RoutingContext> {
    return Handler<RoutingContext> { errorContext ->
      if (errorContext.response().ended()) {
        // Ignore completed response
        return@Handler
      }
      logger.error("Route Error:", errorContext.failure())
      errorContext.response()
        .setStatusCode(500)
        .end(JsonObject().put("message", "Something went wrong :(").toBuffer())
    }
  }
}
