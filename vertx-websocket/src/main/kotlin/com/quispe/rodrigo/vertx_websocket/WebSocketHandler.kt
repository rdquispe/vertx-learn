package com.quispe.rodrigo.vertx_websocket

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.http.WebSocketFrame
import org.slf4j.LoggerFactory

class WebSocketHandler constructor(vertx: Vertx) : Handler<ServerWebSocket> {

  val LOG = LoggerFactory.getLogger(WebSocketHandler::class.java)
  var broadcast: PriceBroadcast

  companion object {
    const val PATH = "/ws/simple/prices"
  }

  init {
    broadcast = PriceBroadcast(vertx)
  }

  override fun handle(ws: ServerWebSocket) {
    if (! PATH.equals(ws.path(), ignoreCase = true)) {
      LOG.info("Rejected wrong path: {}", ws.path())
      ws.writeFinalTextFrame("Wrong path. Only $PATH is accepted!")
      closeClient(ws)
      return
    }

    LOG.info("Opening web socket connection: {} {}", ws.path(), ws.textHandlerID())
    ws.accept()
    ws.frameHandler(frameHandler(ws))
    ws.endHandler { onClose ->
      LOG.info("Close {}, {}", ws.textHandlerID())
      broadcast.unregister(ws)
    }
    ws.exceptionHandler { error -> LOG.error("Failed: {}", error) }
    ws.writeTextMessage("Connected!")
    broadcast.register(ws)
  }

  private fun frameHandler(ws: ServerWebSocket): Handler<WebSocketFrame> {
    return Handler<WebSocketFrame> { received: WebSocketFrame ->
      val message: String = received.textData()
      LOG.debug("Received message: {} from client {}", message, ws.textHandlerID())
      if ("disconnect me".equals(message, ignoreCase = true)) {
        LOG.info("Client close requested!")
        closeClient(ws)
      } else {
        ws.writeTextMessage("Not supported => ($message)")
      }
    }
  }

  private fun closeClient(ws: ServerWebSocket) {
    ws.close(1000.toShort(), "Normal Closure")
  }
}
