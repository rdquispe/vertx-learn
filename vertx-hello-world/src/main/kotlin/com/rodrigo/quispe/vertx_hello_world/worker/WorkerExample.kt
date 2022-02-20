package com.rodrigo.quispe.vertx_hello_world.worker

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

class WorkerExample : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(WorkerExample::class.java)

  override fun start(startPromise: Promise<Void>) {
    vertx.deployVerticle(
      WorkerVerticle(), DeploymentOptions()
        .setWorker(true)
        .setWorkerPoolSize(1)
        .setWorkerPoolName("my-worker-verticle")
    )
    startPromise.complete()
    executeBlockingCode()
  }

  private fun executeBlockingCode() {
    vertx.executeBlocking<Unit>({ event ->
      logger.debug("EXECUTE_BLOCKING_CODE")
      Thread.sleep(5000)
      event.complete()
    }, { result ->
      if (result.succeeded()) {
        logger.debug("BLOCKING_CALL_DONE")
      } else {
        logger.debug("BLOCKING_CALL_FAILED_DUE_TO: {}", result.cause().message)
      }
    })
  }
}

fun main(args: Array<String>) {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(WorkerExample())
}
