package com.rodrigo.quispe.vertx_hello_world.eventbus

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory
import java.time.Duration

class Publish : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    startPromise.complete()
    vertx.setPeriodic(Duration.ofSeconds(10).toMillis()) { id ->
      vertx.eventBus().publish(Publish::class.java.name, "A message for everyone!")
    }
  }
}

class Subscribe1 : AbstractVerticle() {
  private val logger = LoggerFactory.getLogger(Subscribe1::class.java)

  override fun start(startPromise: Promise<Void>) {
    startPromise.complete()
    vertx.eventBus().consumer<String>(Publish::class.java.name) { message ->
      logger.debug("RECEIVER: {}", message.body())
    }
  }
}

class Subscribe2 : AbstractVerticle() {
  private val logger = LoggerFactory.getLogger(Subscribe2::class.java)

  override fun start(startPromise: Promise<Void>) {
    startPromise.complete()
    vertx.eventBus().consumer<String>(Publish::class.java.name) { message ->
      logger.debug("RECEIVER: {}", message.body())
    }
  }
}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(Publish())
  vertx.deployVerticle(Subscribe1())
  vertx.deployVerticle(Subscribe2::class.java.name, DeploymentOptions().setInstances(2))
}
