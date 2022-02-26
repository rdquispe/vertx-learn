package com.rodrigo.quispe.vertx_stock_broker.watchlist

import com.rodrigo.quispe.vertx_stock_broker.MainVerticle
import com.rodrigo.quispe.vertx_stock_broker.assets.Asset
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import java.util.UUID

@ExtendWith(VertxExtension::class)
class WatchListRestApiTest {

  val logger = LoggerFactory.getLogger(WatchListRestApiTest::class.java)

  @BeforeEach
  fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(MainVerticle(), testContext.succeeding<String> { _ -> testContext.completeNow() })
  }

  val body = WatchList(listOf(Asset("AMZN"), Asset("TSLA"))).toJsonObject()

  @Test
  fun adds_and_return_watchlist_for_account(vertx: Vertx, testContext: VertxTestContext) {

    val client = WebClient.create(vertx, WebClientOptions().setDefaultPort(MainVerticle.PORT))
    val accountId = UUID.randomUUID().toString()
    client.put("/account/watchlist/$accountId")
      .sendJsonObject(body)
      .onComplete(testContext.succeeding { response ->
        testContext.verify {
          val json = response.bodyAsJsonObject()
          logger.info("Response PUT {}", json)
          Assertions.assertEquals("""{"assets":[{"symbol":"AMZN"},{"symbol":"TSLA"}]}""", json.encode())
          Assertions.assertEquals(200, response.statusCode())
        }
      })
      .compose { next ->
        client.get("/account/watchlist/$accountId")
          .send()
          .onComplete(testContext.succeeding { response ->
            val json = response.bodyAsJsonObject()
            logger.info("Response GET {}", json)
            Assertions.assertEquals("""{"assets":[{"symbol":"AMZN"},{"symbol":"TSLA"}]}""", json.encode())
            Assertions.assertEquals(200, response.statusCode())
            testContext.completeNow()
          })
      }
  }
}
