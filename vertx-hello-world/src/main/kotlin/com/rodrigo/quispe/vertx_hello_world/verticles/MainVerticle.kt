package com.rodrigo.quispe.vertx_hello_world.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import java.util.*

class MainVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    logger.debug("START {}", this.javaClass.canonicalName)
    vertx.deployVerticle(VerticleA())
    vertx.deployVerticle(VerticleB())
    vertx.deployVerticle(VerticleN::class.java.name,
      DeploymentOptions()
        .setInstances(4)
        .setConfig(
          JsonObject()
            .put("id",UUID.randomUUID().toString())
            .put("name",VerticleN::class.java.simpleName)
        )
    )
    startPromise.complete()
  }
}

fun main(args: Array<String>) {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle())
}
