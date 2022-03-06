package com.quispe.rodrigo.vertx_websocket

import io.vertx.core.Handler
import io.vertx.core.http.ServerWebSocket
import org.slf4j.LoggerFactory

class WebSocketHandler : Handler<ServerWebSocket> {

  val LOG = LoggerFactory.getLogger(WebSocketHandler::class.java)

  override fun handle(ws: ServerWebSocket) {
    LOG.info("Opening web socket connection: {} {}", ws.path(), ws.textHandlerID())
    ws.accept()
    ws.endHandler { onClose -> LOG.info("Close {}, {}", ws.textHandlerID()) }
    ws.exceptionHandler { error -> LOG.error("Failed: {}", error) }
    ws.writeTextMessage("Connected!")
  }
}
