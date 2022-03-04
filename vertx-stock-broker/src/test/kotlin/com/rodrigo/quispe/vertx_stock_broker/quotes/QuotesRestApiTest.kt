package com.rodrigo.quispe.vertx_stock_broker.quotes

import com.rodrigo.quispe.vertx_stock_broker.AbstractTest
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(VertxExtension::class)
class QuotesRestApiTest : AbstractTest() {

  @Test
  fun returns_quote_for_asset(vertx: Vertx, testContext: VertxTestContext) {

    val client = webClient(vertx)
    client.get("/quotes/AMZN")
      .send()
      .onComplete(testContext.succeeding { response ->
        testContext.verify {
          val json = response.bodyAsJsonObject()
          assertEquals("""{"symbol":"AMZN"}""", json.getJsonObject("asset").encode())
          assertEquals(200, response.statusCode())
          assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(), response.getHeader(HttpHeaders.CONTENT_TYPE.toString()))
          testContext.completeNow()
        }
      })
  }

  @Test
  fun returns_not_found_for_unknown_asset(vertx: Vertx, testContext: VertxTestContext) {

    val client = webClient(vertx)
    client.get("/quotes/UNKNOWN")
      .send()
      .onComplete(testContext.succeeding { response ->
        testContext.verify {
          val json = response.bodyAsJsonObject()
          assertEquals(404, response.statusCode())
          assertEquals("""{"message":"quote for asset UNKNOWN not available!","path":"/quotes/UNKNOWN"}""", json.encode())
          assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(), response.getHeader(HttpHeaders.CONTENT_TYPE.toString()))
          testContext.completeNow()
        }
      })
  }

  private fun webClient(vertx: Vertx) =
    WebClient.create(vertx, WebClientOptions().setDefaultPort(TEST_SERVER_PORT))
}
