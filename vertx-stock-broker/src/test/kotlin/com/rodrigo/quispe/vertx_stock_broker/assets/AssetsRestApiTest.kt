package com.rodrigo.quispe.vertx_stock_broker.assets

import com.rodrigo.quispe.vertx_stock_broker.MainVerticle
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(VertxExtension::class)
class AssetsRestApiTest {

  @BeforeEach
  fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(MainVerticle(), testContext.succeeding<String> { _ -> testContext.completeNow() })
  }

  @Test
  fun returns_all_assets(vertx: Vertx, testContext: VertxTestContext) {

    val client = WebClient.create(vertx, WebClientOptions().setDefaultPort(MainVerticle.PORT))
    client.get("/assets")
      .send()
      .onComplete(testContext.succeeding { response ->
        testContext.verify {
          val json = response.bodyAsJsonArray()
          assertEquals("""[{"symbol":"AAPL"},{"symbol":"AMZN"},{"symbol":"NFLX"},{"symbol":"TSLA"}]""", json.encode())
          assertEquals(200, response.statusCode())
          testContext.completeNow()
        }
      })
  }
}
