package com.rodrigo.quispe.vertx_stock_broker

import com.rodrigo.quispe.vertx_stock_broker.assets.AssetsRestApi
import com.rodrigo.quispe.vertx_stock_broker.quotes.QuotesRestApi
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory


class MainVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

  companion object {
    const val PORT = 8888
  }

  override fun start(startPromise: Promise<Void>) {
    val restApi = Router.router(vertx)
    restApi.route().failureHandler { errorContext ->
      if (errorContext.response().ended()) {
        return@failureHandler
      }
      logger.error("ROUTE_ERROR", errorContext.failure())
      errorContext.response()
        .setStatusCode(500)
        .end(JsonObject().put("message", "Something when error :(").toBuffer())
    }
    AssetsRestApi.attach(restApi)
    QuotesRestApi.attach(restApi)

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
