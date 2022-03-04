package com.rodrigo.quispe.vertx_stock_broker

import com.rodrigo.quispe.vertx_stock_broker.config.ConfigLoader
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach

abstract class AbstractTest {

  companion object {
    val TEST_SERVER_PORT = 9000
  }

  @BeforeEach
  fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
    System.setProperty(ConfigLoader.SERVER_PORT, TEST_SERVER_PORT.toString())
    vertx.deployVerticle(MainVerticle(), testContext.succeeding<String> { _ -> testContext.completeNow() })
  }
}
