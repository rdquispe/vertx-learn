package com.rodrigo.quispe.vertx_hello_world.futurepromise

import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
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
      .onSuccess(context::failNow)
      .onFailure { error ->
        logger.debug("ERROR: {}", error.message)
        context.completeNow()
      }
  }

  @Test
  fun futureMap(vertx: Vertx, context: VertxTestContext) {
    logger.debug("START")
    val promise = Promise.promise<String>()
    vertx.setTimer(500) { id ->
      promise.complete("PROMISE_SUCCESS")
      logger.debug("TIMER_DONE")
    }

    val future = promise.future()
    future
      .map { asString ->
        logger.debug("MAP_STRING_TO_JSON_OBJECT: {}", asString)
        JsonObject().put("key", asString)
      }
      .map { jsonObject -> JsonArray().add(jsonObject) }
      .onSuccess { result ->
        logger.debug("RESULT: {}", result)
        context.completeNow()
      }
      .onFailure(context::failNow)
  }

  @Test
  fun futureCoordination(vertx: Vertx, context: VertxTestContext) {
    vertx.createHttpServer()
      .requestHandler { request -> logger.debug("{}", request) }
      .listen(10_000)
      .compose { server ->
        logger.info("ANOTHER_TASK")
        Future.succeededFuture<HttpServer>(server)
      }
      .compose { server ->
        logger.info("EVEN_MORE")
        Future.succeededFuture<HttpServer>(server)
      }
      .onFailure(context::failNow)
      .onSuccess { server ->
        logger.debug("SERVER_STARTER_ON_PORT {}", server.actualPort())
        context.completeNow()
      }
  }

  @Test
  fun futureComposition(vertx: Vertx, context: VertxTestContext) {
    val one = Promise.promise<Void>()
    val two = Promise.promise<Void>()
    val three = Promise.promise<Void>()

    val futureOne = one.future()
    val futureTwo = two.future()
    val futureThree = three.future()

    CompositeFuture.all(futureOne, futureTwo, futureThree)
      .onFailure(context::failNow)
      .onSuccess{
        logger.debug("SUCCESS")
        context.completeNow()
      }

    // Complete futures
    // Note: All futures are completed otherwise Error
    vertx.setTimer(500) { id ->
      one.complete()
      two.complete()
      three.complete()
    }
  }
}
