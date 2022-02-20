package com.rodrigo.quispe.vertx_hello_world.eventbus

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

class Sender : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    startPromise.complete()
    vertx.setPeriodic(1000) { id ->
      // Send message every second
      vertx.eventBus().send(Sender::class.java.name, "Sending message...")
    }
  }
}

class Receiver : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(ResponseVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    startPromise.complete()
    vertx.eventBus().consumer<String>(Sender::class.java.name) { message ->
      logger.debug("RECEIVED_MESSAGE: {}", message.body())
    }
  }
}


fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(Sender())
  vertx.deployVerticle(Receiver())
}
