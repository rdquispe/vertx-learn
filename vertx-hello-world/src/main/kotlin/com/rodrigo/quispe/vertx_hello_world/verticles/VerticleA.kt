package com.rodrigo.quispe.vertx_hello_world.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.LoggerFactory

class VerticleA : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(VerticleA::class.java)

  override fun start(startPromise: Promise<Void>) {

    logger.debug("START {}", this.javaClass.canonicalName)
    vertx.deployVerticle(VerticleAA()) { whenDeployed ->
      logger.debug("DEPLOYED {}", VerticleAA::class.simpleName)
      vertx.undeploy(whenDeployed.result())
    }
    vertx.deployVerticle(VerticleAB()) { whenDeployed ->
      logger.debug("DEPLOYED {}", VerticleAB::class.simpleName)
      // Do not undeployed
    }
    startPromise.complete()
  }
}
