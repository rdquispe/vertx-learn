package com.quispe.rodrigo.vertx_websocket

import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.WebSocket
import io.vertx.core.http.WebSocketConnectOptions
import io.vertx.junit5.Timeout
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


@ExtendWith(VertxExtension::class)
class TestMainVerticle {

  private val LOG: Logger = LoggerFactory.getLogger(TestMainVerticle::class.java)
  val EXPECTED_MESSAGES = 5

  @BeforeEach
  fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(MainVerticle(), testContext.succeeding<String> { _ -> testContext.completeNow() })
  }

  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @Test
  fun can_connect_to_web_socket_server(vertx: Vertx, context: VertxTestContext) {
    val client = vertx.createHttpClient()
    client.webSocket(8900, "localhost", WebSocketHandler.PATH)
      .onFailure { t: Throwable? -> context.failNow(t) }
      .onComplete(context.succeeding { ws: WebSocket ->
        ws.handler { data: Buffer ->
          val receivedData = data.toString()
          LOG.debug("Received message: {}", receivedData)
          assertEquals("Connected!", receivedData)
          client.close()
          context.completeNow()
        }
      })
  }

  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @Test
  fun can_receive_multiple_messages(vertx: Vertx, context: VertxTestContext) {
    val client = vertx.createHttpClient()
    val counter = AtomicInteger(0)
    client.webSocket(
      WebSocketConnectOptions()
        .setHost("localhost")
        .setPort(8900)
        .setURI(WebSocketHandler.PATH)
    )
      .onFailure { error -> context.failNow(error) }
      .onComplete(context.succeeding { ws: WebSocket ->
        ws.handler { data: Buffer ->
          val receivedData = data.toString()
          LOG.debug("Received message: {}", receivedData)
          val currentValue = counter.getAndIncrement()
          if (currentValue >= EXPECTED_MESSAGES) {
            client.close()
            context.completeNow()
          } else {
            LOG.debug("not enough messages yet... ({}/{})", currentValue, EXPECTED_MESSAGES)
          }
        }
      })
  }
}
