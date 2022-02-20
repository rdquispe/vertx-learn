package com.rodrigo.quispe.vertx_hello_world.worker

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

class WorkerVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(WorkerVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {

    logger.debug("DEPLOYED_AS_WORKER_VERTICLE")
    startPromise.complete()
    logger.debug("BLOCKING_OPERATION_DONE")
  }
}
