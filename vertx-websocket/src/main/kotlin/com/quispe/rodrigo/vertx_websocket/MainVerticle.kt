package com.quispe.rodrigo.vertx_websocket

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.LoggerFactory


class MainVerticle : AbstractVerticle() {

  val LOG = LoggerFactory.getLogger(MainVerticle::class.java)

  companion object {
    const val PORT = 8900
  }

  override fun start(startPromise: Promise<Void>) {
    vertx
      .createHttpServer()
      .webSocketHandler(WebSocketHandler(vertx))
      .listen(PORT) { http ->
        if (http.succeeded()) {
          startPromise.complete()
          LOG.info("HTTP server started on port $PORT")
        } else {
          startPromise.fail(http.cause());
        }
      }
  }
}
