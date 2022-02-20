package com.rodrigo.quispe.vertx_hello_world.eventbus

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory


private const val ADDRESS = "my.request.address"

class RequestVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(RequestVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    startPromise.complete()
    val eventBus = vertx.eventBus()
    val message = "Hello World!"
    logger.debug("SENDING: {}", message)
    eventBus.request<String>(ADDRESS, message) { reply ->
      logger.debug("RESPONSE: {}", reply.result().body())
    }
  }
}

class ResponseVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(ResponseVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    startPromise.complete()
    vertx.eventBus().consumer<String>(ADDRESS) { message ->
      logger.debug("RECEIVED_MESSAGE: {}", message.body())
      message.reply("RECEIVED_YOUR_MESSAGE THANKS!")
    }
  }
}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(RequestVerticle())
  vertx.deployVerticle(ResponseVerticle())
}
