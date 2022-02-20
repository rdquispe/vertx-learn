package com.rodrigo.quispe.vertx_hello_world.futurepromise

import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory

@ExtendWith(VertxExtension::class)
class FuturePromiseExample {

  private val logger = LoggerFactory.getLogger(FuturePromiseExample::class.java)

  @Test
  fun promiseSuccess(vertx: Vertx, context: VertxTestContext) {
    logger.debug("START")
    val promise = Promise.promise<String>()
    vertx.setTimer(500) { id ->
      promise.complete()
      logger.debug("SUCCESS")
      context.completeNow()
    }
    logger.debug("END")
  }

  @Test
  fun promiseFailure(vertx: Vertx, context: VertxTestContext) {
    logger.debug("START")
    val promise = Promise.promise<String>()
    vertx.setTimer(500) { id ->
      promise.fail(java.lang.RuntimeException("Fail!!"))
      logger.debug("FAILED")
      context.completeNow()
    }
    logger.debug("END")
  }

  @Test
  fun futureSuccess(vertx: Vertx, context: VertxTestContext) {
    logger.debug("START")
    val promise = Promise.promise<String>()
    vertx.setTimer(500) { id ->
      promise.complete("PROMISE_SUCCESS")
      logger.debug("TIMER_DONE")
    }

    val future = promise.future()
    future
      .onSuccess { result ->
        logger.debug("RESULT: {}", result)
        context.completeNow()
      }
      .onFailure(context::failNow)
  }

  @Test
  fun futureFailure(vertx: Vertx, context: VertxTestContext) {
    logger.debug("START")
    val promise = Promise.promise<String>()
    vertx.setTimer(500) { id ->
      promise.fail(java.lang.RuntimeException("Fail!!"))
      logger.debug("TIMER_DONE")
    }

    val future = promise.future()
    future
      .onSuccess (context::failNow)
      .onFailure{ error ->
        logger.debug("ERROR: {}", error.message)
        context.completeNow()
      }
  }
}
