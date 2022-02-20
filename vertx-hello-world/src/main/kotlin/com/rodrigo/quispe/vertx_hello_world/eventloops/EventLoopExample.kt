package com.rodrigo.quispe.vertx_hello_world.eventloops

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class EventLoopExample : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(EventLoopExample::class.java)

  override fun start(startPromise: Promise<Void>) {
    logger.debug("START {}", this.javaClass.canonicalName)
    startPromise.complete()
    // Do not this inside a verticle
    Thread.sleep(5000)
  }
}

fun main(args: Array<String>) {
  val vertx = Vertx.vertx(
    VertxOptions()
      .setMaxEventLoopExecuteTime(500)
      .setMaxEventLoopExecuteTimeUnit(TimeUnit.MILLISECONDS)
      .setBlockedThreadCheckInterval(1)
      .setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS)
      .setEventLoopPoolSize(2)
  )
  vertx.deployVerticle(EventLoopExample::class.java.name, DeploymentOptions().setInstances(4))
}
