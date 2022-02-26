package com.rodrigo.quispe.vertx_stock_broker

import com.rodrigo.quispe.vertx_stock_broker.assets.AssetsRestApi
import com.rodrigo.quispe.vertx_stock_broker.quotes.QuotesRestApi
import com.rodrigo.quispe.vertx_stock_broker.watchlist.WatchListRestApi
import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import org.slf4j.LoggerFactory


class MainVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

  companion object {
    const val PORT = 8888
  }

  override fun start(startPromise: Promise<Void>) {
    val restApi = Router.router(vertx)
    restApi.route()
      .handler(BodyHandler.create())
      .failureHandler(handleFailure())
    AssetsRestApi.attach(restApi)
    QuotesRestApi.attach(restApi)
    WatchListRestApi.attach(restApi)

    vertx
      .createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler { error -> logger.error("HTTP_ERROR_SERVER", error) }
      .listen(PORT) { http ->
        if (http.succeeded()) {
          startPromise.complete()
          logger.info("HTTP server started on port 8888")
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

fun main() {
  val vertx = Vertx.vertx()
  vertx.exceptionHandler { error ->
    println("UNHANDLED: $error")
  }
  vertx.deployVerticle(MainVerticle()) { ar ->
    if (ar.failed()) {
      println("FAILED_DEPLOY: ${ar.cause()}")
    }
    println("DEPLOYED MainVerticle!")
  }
}
