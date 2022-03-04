package com.rodrigo.quispe.vertx_stock_broker

import com.rodrigo.quispe.vertx_stock_broker.config.ConfigLoader
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory

abstract class AbstractTest {

  private val logger = LoggerFactory.getLogger(AbstractTest::class.java)

  companion object {
    val TEST_SERVER_PORT = 9000
  }

  @BeforeEach
  fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
    System.setProperty(ConfigLoader.SERVER_PORT, TEST_SERVER_PORT.toString())
    System.setProperty(ConfigLoader.DB_HOST, "localhost")
    System.setProperty(ConfigLoader.DB_PORT, "5432")
    System.setProperty(ConfigLoader.DB_DATABASE, "vertx-stock-broker")
    System.setProperty(ConfigLoader.DB_USER, "postgres")
    System.setProperty(ConfigLoader.DB_PASSWORD, "secret")
    logger.info("TEST_USING_ARE_LOCAL_DATABASE!!!")
    vertx.deployVerticle(MainVerticle(), testContext.succeeding<String> { _ -> testContext.completeNow() })
  }
}
