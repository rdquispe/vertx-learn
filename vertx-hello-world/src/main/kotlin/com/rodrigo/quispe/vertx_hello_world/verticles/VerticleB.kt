package com.rodrigo.quispe.vertx_hello_world.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.LoggerFactory

class VerticleB : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(VerticleB::class.java)

  override fun start(startPromise: Promise<Void>) {

    logger.debug("START {}", this.javaClass.canonicalName)
    startPromise.complete()
  }
}
