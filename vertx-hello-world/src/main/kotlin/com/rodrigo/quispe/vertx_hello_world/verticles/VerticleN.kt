package com.rodrigo.quispe.vertx_hello_world.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.LoggerFactory

class VerticleN : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(VerticleN::class.java)

  override fun start(startPromise: Promise<Void>) {

    logger.debug("START {} ON_THE_THREAD {} WITH_CONFIG {}",
      this.javaClass.canonicalName,
      Thread.currentThread().name,
      config().toString()
    )
    startPromise.complete()
  }
}
