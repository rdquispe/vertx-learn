package com.quispe.rodrigo.vertx_websocket

import io.vertx.core.Vertx
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.Random


class PriceBroadcast constructor(vertx: Vertx) {

  private val LOG: Logger = LoggerFactory.getLogger(PriceBroadcast::class.java)
  private val connectedClients: MutableMap<String, ServerWebSocket> = mutableMapOf()

  init {
    periodicUpdate(vertx)
  }

  fun periodicUpdate(vertx: Vertx) {
    vertx.setPeriodic(Duration.ofSeconds(1).toMillis()) { id ->
      LOG.info("Push update to {} client(s)!", connectedClients.size)
      val priceUpdate = JsonObject()
        .put("symbol", "AMZN")
        .put("value", Random().nextInt(1000))
        .toString()
      connectedClients.values.forEach { ws ->
        ws.writeTextMessage(priceUpdate)
      }
    }
  }

  fun register(ws: ServerWebSocket) {
    connectedClients[ws.textHandlerID()] = ws
  }

  fun unregister(ws: ServerWebSocket) {
    connectedClients.remove(ws.textHandlerID())
  }

}
