package com.rodrigo.quispe.vertx_stock_broker

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    val restApi = Router.router(vertx)
    AssetsRestApi.attach(restApi)
    vertx
      .createHttpServer()
      .requestHandler (restApi)
      .exceptionHandler { error -> logger.error("HTTP_ERROR_SERVER", error) }
      .listen(8888) { http ->
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
