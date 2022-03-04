package com.rodrigo.quispe.vertx_stock_broker.watchlist

import com.rodrigo.quispe.vertx_stock_broker.AbstractTest
import com.rodrigo.quispe.vertx_stock_broker.assets.Asset
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import java.util.UUID

@ExtendWith(VertxExtension::class)
class WatchListRestApiTest : AbstractTest() {

  val logger = LoggerFactory.getLogger(WatchListRestApiTest::class.java)
  val body = WatchList(listOf(Asset("AMZN"), Asset("TSLA"))).toJsonObject()

  @Test
  fun adds_and_return_watchlist_for_account(vertx: Vertx, testContext: VertxTestContext) {

    val client = webClient(vertx)
    val accountId = UUID.randomUUID().toString()
    client.put("/account/watchlist/$accountId")
      .sendJsonObject(body)
      .onComplete(testContext.succeeding { response ->
        testContext.verify {
          val json = response.bodyAsJsonObject()
          logger.info("Response PUT {}", json)
          Assertions.assertEquals("""{"assets":[{"symbol":"AMZN"},{"symbol":"TSLA"}]}""", json.encode())
          Assertions.assertEquals(200, response.statusCode())
          Assertions.assertEquals(
            HttpHeaderValues.APPLICATION_JSON.toString(),
            response.getHeader(HttpHeaders.CONTENT_TYPE.toString())
          )
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
            Assertions.assertEquals(
              HttpHeaderValues.APPLICATION_JSON.toString(),
              response.getHeader(HttpHeaders.CONTENT_TYPE.toString())
            )
            testContext.completeNow()
          })
        Future.succeededFuture<Any>()
      }
  }

  @Test
  fun adds_and_deletes_watchlist_for_account(vertx: Vertx, testContext: VertxTestContext) {

    val client = webClient(vertx)
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
        client.delete("/account/watchlist/$accountId")
          .send()
          .onComplete(testContext.succeeding { response ->
            val json = response.bodyAsJsonObject()
            logger.info("Response DELETE {}", json)
            Assertions.assertEquals("""{"assets":[{"symbol":"AMZN"},{"symbol":"TSLA"}]}""", json.encode())
            Assertions.assertEquals(200, response.statusCode())
            Assertions.assertEquals(
              HttpHeaderValues.APPLICATION_JSON.toString(),
              response.getHeader(HttpHeaders.CONTENT_TYPE.toString())
            )
            testContext.completeNow()
          })
        Future.succeededFuture<Any>()
      }
  }

  private fun webClient(vertx: Vertx) =
    WebClient.create(vertx, WebClientOptions().setDefaultPort(TEST_SERVER_PORT))
}
