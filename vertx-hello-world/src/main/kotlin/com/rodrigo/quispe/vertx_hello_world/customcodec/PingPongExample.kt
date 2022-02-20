package com.rodrigo.quispe.vertx_hello_world.customcodec

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory


class PingVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(PingVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    val eventBus = vertx.eventBus()
    val message = Ping("Hello", true)
    logger.debug("SENDING: {}", message)
    eventBus.registerDefaultCodec(Ping::class.java, LocalMessageCodec<Ping>(Ping::class.java))
    eventBus.request<Pong>(PingVerticle::class.java.name, message) { reply ->
      if (reply.failed()) {
        logger.error("FAILED {}", reply.cause().message)
      }
      logger.debug("RESPONSE: {}", reply.result().body())
    }
    startPromise.complete()
  }
}

class PongVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(PongVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    vertx.eventBus().registerDefaultCodec(Pong::class.java, LocalMessageCodec<Pong>(Pong::class.java))
    vertx.eventBus().consumer<Ping>(PingVerticle::class.java.name) { message ->
      logger.debug("RECEIVED_MESSAGE: {}", message.body())
      message.reply(Pong(0))
    }.exceptionHandler { error ->
      logger.error(error.message)
    }
    startPromise.complete()
  }
}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(PingVerticle()) { ar ->
    if (ar.failed())
      println(ar.cause().message)
  }
  vertx.deployVerticle(PongVerticle()) { ar ->
    if (ar.failed())
      println(ar.cause().message)
  }
}
